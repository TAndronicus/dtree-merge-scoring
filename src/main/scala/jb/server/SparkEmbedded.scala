package jb.server

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkEmbedded {

    val conf = new SparkConf().setAppName("dtree-merge").setMaster("local")
    val ss = SparkSession.builder.config(conf).getOrCreate

}
