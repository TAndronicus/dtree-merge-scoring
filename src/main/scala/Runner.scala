import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object Runner {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("dtree-merge").setMaster("local")
    val ss = SparkSession.builder().config(conf).getOrCreate()
    ss.read.format("csv")
    while (true) {}

  }

}
