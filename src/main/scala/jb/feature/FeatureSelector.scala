package jb.feature

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.feature.VectorAssembler

import jb.util.Const._

object FeatureSelector {

  def getVectorizedInput(cols: Array[String]): Transformer = {
    new VectorAssembler().setInputCols(cols.take(cols.length - 1)).setOutputCol(SPARSE_FEATURES)
  }

}
