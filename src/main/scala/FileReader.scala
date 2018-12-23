import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.DataFrame
import server.SparkEmbedded

object FileReader {

  def getRawInput(path: String, format: String): DataFrame = {
    SparkEmbedded.ss.read.option("inferSchema", "true").format("csv").load("A/data_banknote_authentication.csv")
  }

  def getVectorizedInput(path: String, format: String): DataFrame = {
    val input = FileReader.getRawInput("A/biodeg.csv", "csv")
    val cols = input.columns
    val assembler = new VectorAssembler().setInputCols(cols.take(cols.size - 1)).setOutputCol("sparseFeatures")
    assembler.transform(input).withColumnRenamed(cols.last, "label")
  }

}
