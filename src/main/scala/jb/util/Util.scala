package jb.util

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import jb.server.SparkEmbedded
import jb.util.Const._
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.feature.ChiSqSelectorModel
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DoubleType, LongType, StructField, StructType}

import scala.collection.mutable

object Util {

  def getExtrema(input: DataFrame, selectedFeatures: Array[Int]): (Array[Double], Array[Double]) = {
    var paramMap = List.newBuilder[(String, String)]
    for (item <- selectedFeatures.sorted; fun <- Array("min", "max")) {
      paramMap += (COL_PREFIX + item -> fun)
    }
    val extrema = input.agg(paramMap.result().head, paramMap.result().drop(1): _*).head.toSeq.toIndexedSeq
    val mins = extrema.sliding(1, 2).flatten.map(value => parseDouble(value)).toArray
    val maxs = extrema.drop(1).sliding(1, 2).flatten.map(value => parseDouble(value)).toArray
    (mins, maxs)
  }

  def parseDouble(value: Any): Double = {
    value match {
      case int: Int =>
        int.toDouble
      case double: Double =>
        double
    }
  }

  def optimizeInput(input: DataFrame, dataPrepModel: PipelineModel): DataFrame = {
    dataPrepModel.transform(input).select(
      Util.getSelectedFeatures(dataPrepModel).map(
        item => col(COL_PREFIX + item)
      ).+:(col(FEATURES)).+:(col(LABEL)): _*
    ).persist
  }

  def getSelectedFeatures(dataPrepModel: PipelineModel): Array[Int] = {
    dataPrepModel.stages(1).asInstanceOf[ChiSqSelectorModel].selectedFeatures
  }

  def recacheInput2Subsets(input: DataFrame, subsets: Array[DataFrame]): Unit = {
    input.unpersist
    subsets.foreach(_.cache)
  }

  def clearCache(subsets: Array[Dataset[Row]]) = {
    subsets.foreach(_.unpersist)
  }

  def dispenseSubsets(subsets: Array[DataFrame]): (Array[DataFrame], DataFrame, DataFrame) = {
    val trainingSubsets = subsets.take(subsets.length - 2)
    val cvSubset = subsets(subsets.length - 2)
    val testSubset = subsets.last
    (trainingSubsets, cvSubset, testSubset)
  }

  def getElCubeSize(mins: Array[Double], maxes: Array[Double], division: Int): Array[Double] = {
    mins.indices.map(i => (maxes(i) - mins(i)) / division).toArray
  }

  def calculateMomentsByLabels(input: DataFrame, selectedFeatures: Array[Int]): Map[Double, Array[Double]] = {
    val selFNames = selectedFeatures.map(item => COL_PREFIX + item)
    val intermediate = input.select(selFNames.map(col).+:(col(LABEL)): _*).groupBy(col(LABEL)).avg(selFNames: _*)
    val moments = mutable.Map[Double, Array[Double]]()
    for (row <- intermediate.collect()) {
      moments.put(parseDouble(row.get(0)), row.toSeq.takeRight(row.length - 1).toArray.map(parseDouble))
    }
    moments.toMap
  }

  def calculateMomentsByPrediction(input: DataFrame, selectedFeatures: Array[Int], baseModels: Array[DecisionTreeClassificationModel]): DataFrame = {
    var dataset = input.select(col("*"))
    val selFNames = selectedFeatures.map(item => COL_PREFIX + item)
    val schema = StructType(Seq(
      StructField(PREDICTION, DoubleType, nullable = false),
      StructField(PREDICTION + COUNT_SUFFIX, LongType, nullable = false)
    ) ++ selFNames.map(_ + AVERAGE_SUFFIX).map(item => StructField(item, DoubleType, nullable = true)))
    var aggregate = SparkEmbedded.ss.createDataFrame(SparkEmbedded.ss.sparkContext.emptyRDD[Row], schema)

    for (index <- baseModels.indices) {
      dataset = baseModels(index).setPredictionCol(PREDICTION + "_" + index).transform(dataset).drop(COLUMNS2DROP: _*)
      aggregate = aggregate.union(dataset.withColumnRenamed(PREDICTION + "_" + index, PREDICTION).groupBy(PREDICTION).agg(count(PREDICTION), selFNames.map(avg): _*))
    }
    aggregate.groupBy(PREDICTION).agg(sum(PREDICTION + COUNT_SUFFIX), selFNames.map(_ + AVERAGE_SUFFIX).map(col).map(_ * col(PREDICTION + COUNT_SUFFIX)).map(sum): _*)
  }

  def calculateMomentsByPredictionCollectively(input: DataFrame, selectedFeatures: Array[Int], baseModels: Array[DecisionTreeClassificationModel]): Map[Double, Array[Double]] = {
    val weightedMean = calculateMomentsByPrediction(input, selectedFeatures, baseModels)
    val moments = mutable.Map[Double, Array[Double]]()
    for (row <- weightedMean.collect()) {
      moments.put(parseDouble(row.getDouble(0)), row.toSeq.takeRight(row.length - 2).toArray.map(parseDouble).map(_ / row.getLong(1)))
    }
    moments.toMap
  }

  def calculateMomentsByPredictionRespectively(input: Array[DataFrame], selectedFeatures: Array[Int], baseModels: Array[DecisionTreeClassificationModel]): Map[Double, Array[Double]] = {
    var first = LocalDateTime.now
    val selFNames = selectedFeatures.map(item => COL_PREFIX + item)
    val schema = StructType(Seq(
      StructField(PREDICTION, DoubleType, nullable = false),
      StructField("sum(" + PREDICTION + COUNT_SUFFIX + ")", LongType, nullable = false)
    ) ++ selFNames.map("sum((" + _ + AVERAGE_SUFFIX + " * " + PREDICTION + COUNT_SUFFIX + "))").map(item => StructField(item, DoubleType, nullable = true)))
    var aggregate = SparkEmbedded.ss.createDataFrame(SparkEmbedded.ss.sparkContext.emptyRDD[Row], schema)
    var second = LocalDateTime.now
    println(s"Preparation: ${ChronoUnit.MILLIS.between(first, second)}")
    first = LocalDateTime.now
    for (index <- baseModels.indices) {
      val baseMoments = calculateMomentsByPrediction(input(index), selectedFeatures, Array(baseModels(index)))
      aggregate = aggregate.union(baseMoments)
    }
    second = LocalDateTime.now
    println(s"Singular mappings: ${ChronoUnit.MILLIS.between(first, second)}")
    first = LocalDateTime.now
    val weightedMoments = aggregate.groupBy(PREDICTION).agg(sum("sum(" + PREDICTION + COUNT_SUFFIX + ")"),
      selFNames.map("sum((" + _ + AVERAGE_SUFFIX + " * " + PREDICTION + COUNT_SUFFIX + "))").map(col).map(sum):_*)
    second = LocalDateTime.now
    println(s"Aggregation: ${ChronoUnit.MILLIS.between(first, second)}")
    first = LocalDateTime.now
    var moments = mutable.Map[Double, Array[Double]]()
    for (row <- weightedMoments.collect()) {
      moments.put(parseDouble(row.getDouble(0)), row.toSeq.takeRight(row.length - 2).toArray.map(parseDouble).map(_ / row.getLong(1)))
    }
    second = LocalDateTime.now
    println(s"Composition: ${ChronoUnit.MILLIS.between(first, second)}")
    moments.toMap
  }

}
