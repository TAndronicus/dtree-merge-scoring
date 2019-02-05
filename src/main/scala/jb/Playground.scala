package jb

import jb.server.SparkEmbedded
import org.apache.spark.sql.functions.col

object Playground {

  def main(args: Array[String]): Unit = {
    val df = SparkEmbedded.ss.createDataFrame(Seq(
      (1, 2, 5),
      (2, 4, 7),
      (3, 6, 9)
    )).toDF("product_id", "min", "max")
    import SparkEmbedded.ss.implicits._
    val cloneDf = df.select(df.columns.map(col): _*)
      .withColumnRenamed("product_id", "product_id1")
      .withColumnRenamed("min", "min1")
      .withColumnRenamed("max", "max1")
    df.crossJoin(cloneDf)
      .where($"product_id" < $"product_id1")
      .where($"min" < $"max1")
      .where($"min1" < $"max").show()
  }

}
