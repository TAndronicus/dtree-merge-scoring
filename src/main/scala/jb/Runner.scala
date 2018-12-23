package jb

import jb.io.FileReader
import jb.selector.Selector
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, MulticlassClassificationEvaluator}

object Runner {

  def main(args: Array[String]): Unit = {

    val vectorizedInput = FileReader.getVectorizedInput("A/biodeg.csv", "csv")
    val dat = Selector.select_chi_sq(vectorizedInput)

    val Array(trainingData, testData) = dat.randomSplit(Array(0.9, 0.1))

    val dt = new DecisionTreeClassifier().setLabelCol("label").setFeaturesCol("features")
    val model = dt.fit(trainingData)
    val prediction = model.transform(testData)

    val evaluator = new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("prediction")
    print(evaluator.evaluate(prediction))
    val multiEval = new MulticlassClassificationEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("accuracy")
    print(multiEval.evaluate(prediction))
  }

}
