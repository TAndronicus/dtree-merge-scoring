import java.util.stream.IntStream

import org.apache.spark.ml.feature.{Interaction, VectorAssembler}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object Runner {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("dtree-merge").setMaster("local")
    val ss = SparkSession.builder.config(conf).getOrCreate
    var data = ss.read.option("inferSchema", "true").format("csv").load("A/biodeg.csv")
    var cols = data.columns
    var assembler = new VectorAssembler().setInputCols(cols.take(cols.size - 1)).setOutputCol("sparseFeatures")
    var clfData = assembler.transform(data)

    // vector assembler
    // pca / chi2
    clfData.printSchema
    clfData.show
    clfData.select("_c0", "features").show(false)
//    while (true) {}

    val dat = Seq(
      (7, Vectors.dense(0.0, 0.0, 18.0, 1.0), 1.0),
      (8, Vectors.dense(0.0, 1.0, 12.0, 0.0), 0.0),
      (9, Vectors.dense(1.0, 0.0, 15.0, 0.1), 0.0)
    )

    val df = ss.createDataFrame(dat)
    df.show()


  }

}
