package jb

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
    val a = Array(0D, 2)
    val b = Array(1D, 5)
    var c = (a ++ b).map(_/2)
    c.foreach(f => print(f))
  }

}