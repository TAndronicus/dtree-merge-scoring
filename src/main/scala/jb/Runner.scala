package jb

import java.util.stream.IntStream

import jb.io.FileReader.getRawInput
import jb.model.{Edge, MappingModel, PostTrainingCV, PostTrainingTrain, PostTrainingTrainFiltered, PreTraining, Rect}
import jb.model.dt.{IntegratedDecisionTreeModel, MappedIntegratedDecisionTreeModel, SimpleIntegratedDecisionTreeModel}
import jb.parser.TreeParser
import jb.prediction.Predictions.predictBaseClfs
import jb.selector.FeatureSelectors
import jb.server.SparkEmbedded
import jb.tester.FullTester.{testI, testMv}
import jb.util.Const._
import jb.util.Util._
import jb.util.functions.DistMappingFunctions._
import jb.vectorizer.FeatureVectorizers.getFeatureVectorizer
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}

class Runner(val nClassif: Int, var nFeatures: Int, val alpha: Double, val mappingModel: MappingModel) {

  def calculateMvIScores(filename: String): Array[Double] = {

    //    import SparkEmbedded.ss.implicits._
    SparkEmbedded.ss.sqlContext.clearCache()

    var input = getRawInput(filename, "csv")
    if (nFeatures > input.columns.length - 1) {
      this.nFeatures = input.columns.length - 1
      println(s"Setting nFeatures to $nFeatures")
    }
    val featureVectorizer = getFeatureVectorizer(input.columns)
    val featureSelector = FeatureSelectors.get_chi_sq_selector(nFeatures)
    val dataPrepPipeline = new Pipeline().setStages(Array(featureVectorizer, featureSelector))
    val dataPrepModel = dataPrepPipeline.fit(input)
    input = optimizeInput(input, dataPrepModel)

    val (mins, maxes) = getExtrema(input, getSelectedFeatures(dataPrepModel))

    val nSubsets = nClassif + 2
    val subsets = input.randomSplit(IntStream.range(0, nSubsets).mapToDouble(_ => 1D / nSubsets).toArray)
    recacheInput2Subsets(input, subsets)
    val (trainingSubsets, cvSubset, testSubset) = dispenseSubsets(subsets)

    val dt = new DecisionTreeClassifier().setLabelCol(LABEL).setFeaturesCol(FEATURES)
    val baseModels = trainingSubsets.map(subset => dt.fit(subset))

    val testedSubset = predictBaseClfs(baseModels, testSubset)
    val mvQualityMeasure = testMv(testedSubset, nClassif)

    val edges: Array[Array[Edge]] = getEdges(mins, maxes, baseModels)

    val integratedModel = if (alpha == 1) new SimpleIntegratedDecisionTreeModel(edges, baseModels, simpleMapping) //TODO: Optimized decision tree model for mapping only
    else mappingModel match {
      case PreTraining() => new MappedIntegratedDecisionTreeModel(edges, baseModels, parametrizedMomentMappingFunction(alpha), calculateMomentsByLabels(input, getSelectedFeatures(dataPrepModel)))
      case PostTrainingCV() => new MappedIntegratedDecisionTreeModel(edges, baseModels, parametrizedMomentMappingFunction(alpha), calculateMomentsByPredictionCollectively(cvSubset, getSelectedFeatures(dataPrepModel), baseModels))
      case PostTrainingTrain() => new MappedIntegratedDecisionTreeModel(edges, baseModels, parametrizedMomentMappingFunction(alpha), calculateMomentsByPredictionRespectively(trainingSubsets, getSelectedFeatures(dataPrepModel), baseModels))
      case PostTrainingTrainFiltered() => throw new Exception("not yet implemented")
    }
    val iPredictions = integratedModel.transform(testedSubset)
    val iQualityMeasure = testI(iPredictions, testedSubset)

    clearCache(subsets)

    Array(mvQualityMeasure._1, if (mvQualityMeasure._2.isNaN) 0D else mvQualityMeasure._2,
      iQualityMeasure._1, if (iQualityMeasure._2.isNaN) 0D else iQualityMeasure._2)

  }

  private def getEdges(mins: Array[Double], maxes: Array[Double], baseModels: Array[DecisionTreeClassificationModel]): Array[Array[Edge]] = {
    val rootRect = Rect(mins, maxes)
    val treeParser = new TreeParser()
    val rects = baseModels.map(model => treeParser.dt2rect(rootRect, model.rootNode))
    rects.map(treeParser.rects2edges)
  }

}
