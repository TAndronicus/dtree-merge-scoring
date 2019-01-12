package jb.vectorizer

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.feature.VectorAssembler
import jb.util.Const.SPARSE_PREDICTIONS
import jb.util.Const.PREDICTION

object PredictionVectorizers {

  def getPredictionVectorizer(nClassif: Int): Transformer = {
    val cols = for (i <- 0.until(nClassif)) yield PREDICTION + "_" + i
    new VectorAssembler().setInputCols(cols.toArray).setOutputCol(SPARSE_PREDICTIONS)
  }

}
