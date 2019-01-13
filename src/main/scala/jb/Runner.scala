package jb

import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.stream.IntStream

import jb.Tester.{testIAcc, testMvAcc}
import jb.io.FileReader.getRawInput
import jb.model.{Cube, IntegratedDecisionTreeModel}
import jb.parser.TreeParser
import jb.prediction.Predictions.predictBaseClfs
import jb.selector.FeatureSelectors
import jb.server.SparkEmbedded
import jb.util.Const._
import jb.util.Util._
import jb.util.functions.WeightAggregators._
import jb.util.functions.WithinDeterminers._
import jb.vectorizer.FeatureVectorizers.getFeatureVectorizer
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier

object Runner {

  val nClassif = 5
  val nFeatures = 2
  val division = 10

  def main(args: Array[String]): Unit = {

    SparkEmbedded.setLogWarn()
    //    import SparkEmbedded.ss.implicits._

    val start = LocalTime.now

    var input = getRawInput("A/biodeg.csv", "csv")
    val featureVectorizer = getFeatureVectorizer(input.columns)
    val featureSelector = FeatureSelectors.get_chi_sq_selector(nFeatures)
    val dataPrepPipeline = new Pipeline().setStages(Array(featureVectorizer, featureSelector))
    val dataPrepModel = dataPrepPipeline.fit(input)
    input = optimizeInput(input, dataPrepModel)

    val (mins, maxes) = getExtrema(input, getSelectedFeatures(dataPrepModel))
    val elSize = getElCubeSize(mins, maxes, division)

    val subsets = input.randomSplit(IntStream.range(0, nClassif + 2).mapToDouble(_ => 1D / (nClassif + 2)).toArray)
    recacheInput2Subsets(input, subsets)
    val (trainingSubsets, cvSubset, testSubset) = dispenseSubsets(subsets)

    val dt = new DecisionTreeClassifier().setLabelCol(LABEL).setFeaturesCol(FEATURES)
    val baseModels = trainingSubsets.map(subset => dt.fit(subset))

    var testedSubset = predictBaseClfs(baseModels, testSubset)
    val mvQualityMeasure = testMvAcc(testedSubset, nClassif)

    val rootRect = Cube(mins, maxes)
    val treeParser = new TreeParser(sumOfVolumes, spansMid)
    val rects = baseModels.map(model => treeParser.dt2rect(rootRect, model.rootNode))
    val tree = treeParser.rect2dt(mins, maxes, elSize, 0, 2, rects)
    val integratedModel = new IntegratedDecisionTreeModel(tree)
    val iPredictions = integratedModel.transform(testedSubset)
    val iQualityMeasure = testIAcc(iPredictions, testedSubset)

    print("\nTime: " + ChronoUnit.MILLIS.between(start, LocalTime.now) + "\n")
    print("MV: " + mvQualityMeasure + "\n")
    print("I: " + iQualityMeasure + "\n")

    //        while (true) {}
  }

}
