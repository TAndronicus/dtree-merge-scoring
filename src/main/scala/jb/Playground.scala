package jb

import jb.model.Cube
import jb.server.SparkEmbedded
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Playground {

  def main(args: Array[String]): Unit = {
//    val conf = new SparkConf().setAppName("dtree-merge").setMaster("local")
//    val ss = SparkSession.builder.config(conf).getOrCreate
//    val s1 = SparkEmbedded.ss.read.option("inferSchema", "true").format("csv").load("A/b.csv")
//    val s2 = SparkEmbedded.ss.read.option("inferSchema", "true").format("csv").load("A/a.csv")
//    s1.unionAll(s2).except(s1.intersect(s2)).show()
//    print("### Second ###")
//    s1.except(s2).union(s2.except(s1)).show()
    var a = Map((1, 2), (3, 4), (4, 5))
    var b = Map((1, 2), (2, 3), (5, 6))
    var c = Set(1, 2, 5, 7)
    var d = Set(1, 3, 4, 6, 7)
    print(c ++ d)
  }

}
