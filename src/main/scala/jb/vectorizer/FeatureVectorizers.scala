package jb.vectorizer

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.feature.VectorAssembler

import jb.util.Const._

object FeatureVectorizers {

  def getFeatureVectorizer(cols: Array[String]): Transformer = {
    new VectorAssembler().setInputCols(cols.take(cols.length - 1)).setOutputCol(SPARSE_FEATURES)
  }

}
