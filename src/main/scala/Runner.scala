import java.util.stream.IntStream

import org.apache.spark.ml.feature.{ChiSqSelector, Interaction, VectorAssembler}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.spark_project.dmg.pmml.SparseArray

object Runner {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("dtree-merge").setMaster("local")
    val ss = SparkSession.builder.config(conf).getOrCreate
    import ss.implicits._
    var data = ss.read.option("inferSchema", "true").format("csv").load("A/biodeg.csv")
    var cols = data.columns
    var assembler = new VectorAssembler().setInputCols(cols.take(cols.size - 1)).setOutputCol("sparseFeatures")
    var clfData = assembler.transform(data)

    // vector assembler
    // pca / chi2
//    clfData.printSchema
//    clfData.show
//    clfData.select($"_c0" * 2, $"sparseFeatures").show(false)

    val selector = new ChiSqSelector().
      setNumTopFeatures(2).
      setFeaturesCol("sparseFeatures").
      setLabelCol(cols.last).
      setOutputCol("selectedFeatures")

    val res = selector.fit(clfData).transform(clfData)

    res.select("sparseFeatures", "selectedFeatures", cols.last).show()
    res.printSchema()

  }

}
