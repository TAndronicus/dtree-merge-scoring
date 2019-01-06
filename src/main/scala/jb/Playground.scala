package jb

import org.apache.spark.SparkConf
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

import scala.math._

object Playground {

  def main(args: Array[String]): Unit = {
//    val conf = new SparkConf().setAppName("dtree-merge").setMaster("local")
//    val ss = SparkSession.builder.config(conf).getOrCreate
    for (i <- 0 until 5) {
      print(i)
    }
  }

}
