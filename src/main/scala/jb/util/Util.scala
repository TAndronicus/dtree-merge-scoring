package jb.util

import jb.util.Const._
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.feature.ChiSqSelectorModel
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.col

object Util {

  def getExtrema(input: DataFrame, selectedFeatures: Array[Int]): (Array[Double], Array[Double]) = {
    var paramMap = List.newBuilder[(String, String)]
    for (item <- selectedFeatures.sorted; fun <- Array("min", "max")) {
      paramMap += (COL_PREFIX + item -> fun)
    }
    val extrema = input.agg(paramMap.result().head, paramMap.result().drop(1): _*).head.toSeq.toIndexedSeq
    val mins = extrema.sliding(1, 2).flatten.map(_.asInstanceOf[Double]).toArray
    val maxs = extrema.drop(1).sliding(1, 2).flatten.map(_.asInstanceOf[Double]).toArray
    (mins, maxs)
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

  def dispenseSubsets(subsets: Array[DataFrame]): (Array[DataFrame], DataFrame, DataFrame) = {
    val trainingSubsets = subsets.take(subsets.length - 2)
    val cvSubset = subsets(subsets.length - 2)
    val testSubset = subsets.last
    (trainingSubsets, cvSubset, testSubset)
  }

  def getElCubeSize(mins: Array[Double], maxes: Array[Double], division: Int): Array[Double] = {
    mins.indices.map(i => (maxes(i) - mins(i)) / division).toArray
  }

}
