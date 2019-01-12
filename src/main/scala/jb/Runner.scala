package jb

import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.stream.IntStream

import jb.vectorizer.FeatureVectorizers.getFeatureVectorizer
import jb.io.FileReader.getRawInput
import jb.model.Cube
import jb.parser.TreeParser
import jb.selector.FeatureSelectors
import jb.server.SparkEmbedded
import jb.util.Const._
import jb.util.Util._
import jb.util.functions.WeightAggregators._
import jb.util.functions.WithinDeterminers._
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import jb.vectorizer.PredictionVectorizers.getPredictionVectorizer
import jb.prediction.Predictions.testBaseClfs

object Runner {

  val nClassif = 5
  val nFeatures = 2
  val division = 10

  def main(args: Array[String]): Unit = {

    SparkEmbedded.setLogWarn()
    //    import SparkEmbedded.ss.implicits._

    val start = LocalTime.now

    var input = getRawInput("A/data_banknote_authentication.csv", "csv")
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
    val rootRect = Cube(mins, maxes)

    val dt = new DecisionTreeClassifier().setLabelCol(LABEL).setFeaturesCol(FEATURES)
    val baseModels = trainingSubsets.map(subset => dt.fit(subset))
    var testedSubset = testBaseClfs(baseModels, testSubset)
//    val predictions = baseModels.map(model => model.transform(cvSubset))
//    val evaluator = new BinaryClassificationEvaluator().setLabelCol(LABEL).setRawPredictionCol(PREDICTION)
//
//    val evaluations = predictions.map(prediction => evaluator.evaluate(prediction))
//    evaluations.foreach(ev => print(ev + ", "))

    val cols = for (i <- 0.until(nClassif)) yield PREDICTION + "_" + i
    val kurwa = testedSubset.select(cols.head, cols.takeRight(cols.length - 1):_*).limit(10).collect()
      .map(row => row.toSeq.groupBy(_.asInstanceOf[Double].doubleValue()).mapValues(_.length).reduce((t1, t2) => if (t1._2 > t2._2) t1 else t2)).map(_._1)
    val treeParser = new TreeParser(sumOfVolumes, spansMid)
    val rects = baseModels.map(model => treeParser.dt2rect(rootRect, model.rootNode))
    //    rects.foreach(baseRects => {
    //      print("\nBase clf\n")
    //      baseRects.foreach(rect => print(rect.toString + "\n "))
    //    })

    val tree = treeParser.rect2dt(mins, maxes, elSize, 0, 2, rects)

    print("Time: " + ChronoUnit.MILLIS.between(start, LocalTime.now))

    //    val model = pipeline.fit(trainingData)
    //    val prediction = model.transform(testData)

    //    print(evaluator.evaluate(prediction))
    //    val multiEval = new MulticlassClassificationEvaluator().setLabelCol(LABEL).setPredictionCol(PREDICTION).setMetricName("accuracy")
    //    print(multiEval.evaluate(prediction))

    //        while (true) {}
  }

}
