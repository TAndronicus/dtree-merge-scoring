package jb

import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.stream.IntStream

import jb.feature.FeatureSelector
import jb.io.FileReader
import jb.model.Rect
import jb.parser.TreeParser
import jb.selector.Selector
import jb.server.SparkEmbedded
import jb.util.Const._
import jb.util.Util._
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.sql.functions.col

object Runner {

  val nClassif = 5
  val nFeatures = 2
  val division = 10

  def main(args: Array[String]): Unit = {

    SparkEmbedded.setLogWarn()
    //    import SparkEmbedded.ss.implicits._

    val start = LocalTime.now

    var input = FileReader.getRawInput("A/data_banknote_authentication.csv", "csv")
    val featureVectorizer = FeatureSelector.getVectorizedInput(input.columns)
    val featureSelector = Selector.select_chi_sq(nFeatures)
    val dataPrepPipeline = new Pipeline().setStages(Array(featureVectorizer, featureSelector))
    val dataPrepModel = dataPrepPipeline.fit(input)
    input = optimizeInput(input, dataPrepModel)

    val (mins, maxs) = getExtrema(input, getSelectedFeatures(dataPrepModel))

    val subsets = input.randomSplit(IntStream.range(0, nClassif + 2).mapToDouble(_ => 1D / (nClassif + 2)).toArray)
    recacheInput2Subsets(input, subsets)

    val (trainingSubsets, cvSubset, testSubset) = dispenseSubsets(subsets)
    val rootRect = Rect(mins, maxs)

    val dt = new DecisionTreeClassifier().setLabelCol(LABEL).setFeaturesCol(FEATURES)

    val baseModels = trainingSubsets.map(subset => dt.fit(subset))
    val predictions = baseModels.map(model => model.transform(cvSubset))
    val evaluator = new BinaryClassificationEvaluator().setLabelCol(LABEL).setRawPredictionCol(PREDICTION)

    val evaluations = predictions.map(prediction => evaluator.evaluate(prediction))
    evaluations.foreach(ev => print(ev + ", "))

    val rects = baseModels.map(model => TreeParser.dt2rect(rootRect, model.rootNode))
//    rects.foreach(baseRects => {
//      print("\nBase clf\n")
//      baseRects.foreach(rect => print(rect.toString + "\n "))
//    })

    print("Time take/n: " + ChronoUnit.MILLIS.between(start, LocalTime.now))

    //    val model = pipeline.fit(trainingData)
    //    val prediction = model.transform(testData)

    //    print(evaluator.evaluate(prediction))
    //    val multiEval = new MulticlassClassificationEvaluator().setLabelCol(LABEL).setPredictionCol(PREDICTION).setMetricName("accuracy")
    //    print(multiEval.evaluate(prediction))

    //        while (true) {}
  }

}
