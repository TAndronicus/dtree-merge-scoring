package jb.io

import jb.server.SparkEmbedded
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.DataFrame

import jb.util.Conf._

object FileReader {

  def getRawInput(path: String, format: String): DataFrame = {
    SparkEmbedded.ss.read.option("inferSchema", "true").format(format).load(path)
  }

  def getVectorizedInput(path: String, format: String): DataFrame = {
    val input = FileReader.getRawInput(path, format)
    val cols = input.columns
    val assembler = new VectorAssembler().setInputCols(cols.take(cols.length - 1)).setOutputCol(SPARSE_FEATURES)
    assembler.transform(input).withColumnRenamed(cols.last, LABEL)
  }

}
