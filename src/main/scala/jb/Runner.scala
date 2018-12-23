package jb

import jb.feature.FeatureSelector
import jb.io.FileReader
import jb.selector.Selector
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, MulticlassClassificationEvaluator}
import jb.util.Conf._
import org.apache.spark.ml.Pipeline

object Runner {

  def main(args: Array[String]): Unit = {

    val input = FileReader.getRawInput("A/biodeg.csv", "csv")
    val featureVectorizer = FeatureSelector.getVectorizedInput(input.columns)
    val featureSelector = Selector.select_chi_sq()

    val Array(trainingData, testData) = input.randomSplit(Array(0.9, 0.1))

    val dt = new DecisionTreeClassifier().setLabelCol(LABEL).setFeaturesCol(FEATURES)

    val pipeline = new Pipeline().setStages(Array(featureVectorizer, featureSelector, dt))

    val model = pipeline.fit(trainingData)
    val prediction = model.transform(testData)

    val evaluator = new BinaryClassificationEvaluator().setLabelCol(LABEL).setRawPredictionCol(PREDICTION)
    print(evaluator.evaluate(prediction))
    val multiEval = new MulticlassClassificationEvaluator().setLabelCol(LABEL).setPredictionCol(PREDICTION).setMetricName("accuracy")
    print(multiEval.evaluate(prediction))
  }

}
