package jb.util

import org.apache.spark.sql.DataFrame

object Util {

  def getExtrema(input: DataFrame, selectedFeatures: Array[Int]): (Array[Double], Array[Double]) = {
    var paramMap = List.newBuilder[(String, String)]
    for (item <- selectedFeatures.sorted; fun <- Array("min", "max")) {
      paramMap += Tuple2("_c" + item, fun)
    }
    val extrema = input.agg(paramMap.result().head, paramMap.result().drop(1): _*).head.toSeq.toIndexedSeq
    val mins = extrema.sliding(1, 2).flatten.map(i => i.asInstanceOf[Double]).toArray
    val maxs = extrema.drop(1).sliding(1, 2).flatten.map(i => i.asInstanceOf[Double]).toArray
    (mins, maxs)
  }

}
