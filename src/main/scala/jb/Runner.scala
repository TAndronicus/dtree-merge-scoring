package jb

import java.time.LocalTime
import java.util.stream.IntStream

import jb.io.FileReader.getRawInput
import jb.model.Rect
import jb.model.dt.PreMappingIntegratedDecisionTreeModel
import jb.parser.TreeParser
import jb.prediction.Predictions.predictBaseClfs
import jb.selector.FeatureSelectors
import jb.tester.Tester.{testIAcc, testMvAcc}
import jb.util.Const._
import jb.util.Util._
import jb.util.functions.DistMappingFunctions._
import jb.vectorizer.FeatureVectorizers.getFeatureVectorizer
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier

class Runner(val nClassif: Int, var nFeatures: Int, val alpha: Double) {

  def calculateMvIScores(filename: String): Array[Double] = {

    //    import SparkEmbedded.ss.implicits._
    val start = LocalTime.now

    var input = getRawInput(filename, "csv")
    if (nFeatures > input.columns.length - 1) {
      this.nFeatures = input.columns.length - 1; println(s"Setting nFeatures to $nFeatures")
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
    val mvQualityMeasure = testMvAcc(testedSubset, nClassif)
    var result = Array(mvQualityMeasure)

    val rootRect = Rect(mins, maxes)
    val treeParser = new TreeParser()
    val rects = baseModels.map(model => treeParser.dt2rect(rootRect, model.rootNode))
    val edges = rects.map(treeParser.rects2edges)

    //    val integratedModel = new IntegratedDecisionTreeModel(edges, baseModels, simpleMapping)
    val preMappingMoments = calculateMoments(input, getSelectedFeatures(dataPrepModel))
    val integratedModel = new PreMappingIntegratedDecisionTreeModel(edges, baseModels, preMappingMoments, parametrizedMomentMappingFunction(alpha))
    val iPredictions = integratedModel.transform(testedSubset)
    result :+= testIAcc(iPredictions, testedSubset)

    result
  }

}
