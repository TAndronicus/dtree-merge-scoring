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
import jb.util.Util
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.sql.functions.col

object Runner {

  val nClass = 5

  def main(args: Array[String]): Unit = {

    SparkEmbedded.setLogWarn()
    //    import SparkEmbedded.ss.implicits._

    val start = LocalTime.now

    var input = FileReader.getRawInput("A/data_banknote_authentication.csv", "csv")
    val featureVectorizer = FeatureSelector.getVectorizedInput(input.columns)
    val featureSelector = Selector.select_chi_sq()
    val dataPrepPipeline = new Pipeline().setStages(Array(featureVectorizer, featureSelector))
    val dataPrepModel = dataPrepPipeline.fit(input)
    input = dataPrepModel.transform(input).select(
      Util.getSelectedFeatures(dataPrepModel).map(
        item => col(COL_PREFIX + item)
      ).+:(col(FEATURES)).+:(col(LABEL)): _*
    ).persist
    val (mins, maxs) = Util.getExtrema(input, Util.getSelectedFeatures(dataPrepModel))

    val subsets = input.randomSplit(IntStream.range(0, nClass + 2).mapToDouble(_ => 1D / (nClass + 2)).toArray)
    input.unpersist
    subsets.foreach(_.cache)
    val trainingSubsets = subsets.take(subsets.length - 2)
    val cvSubset = subsets(subsets.length - 2)
    val testSubset = subsets.last

    val rootRect = Rect(mins, maxs)

    val dt = new DecisionTreeClassifier().setLabelCol(LABEL).setFeaturesCol(FEATURES)

    val baseModels = trainingSubsets.map(subset => dt.fit(subset))
    val predictions = baseModels.map(model => model.transform(cvSubset))
    val evaluator = new BinaryClassificationEvaluator().setLabelCol(LABEL).setRawPredictionCol(PREDICTION)

    val evaluations = predictions.map(prediction => evaluator.evaluate(prediction))
    evaluations.foreach(ev => print(ev + ", "))

    val rects = baseModels.map(model => TreeParser.dt2rect(rootRect, model.rootNode))
    rects.foreach(baseRects => {
      print("\nBase clf\n")
      baseRects.foreach(rect => print(rect.toString + "\n "))
    })

    print("Time take/n: " + ChronoUnit.MILLIS.between(start, LocalTime.now))

    //    val model = pipeline.fit(trainingData)
    //    val prediction = model.transform(testData)

    //    print(evaluator.evaluate(prediction))
    //    val multiEval = new MulticlassClassificationEvaluator().setLabelCol(LABEL).setPredictionCol(PREDICTION).setMetricName("accuracy")
    //    print(multiEval.evaluate(prediction))

    //        while (true) {}
  }

}
