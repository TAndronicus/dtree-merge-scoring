package jb.util

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.feature.ChiSqSelectorModel
import org.apache.spark.sql.DataFrame
import jb.util.Const._

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

  def getSelectedFeatures(dataPrepModel: PipelineModel): Array[Int] = {
    dataPrepModel.stages(1).asInstanceOf[ChiSqSelectorModel].selectedFeatures
  }

}
