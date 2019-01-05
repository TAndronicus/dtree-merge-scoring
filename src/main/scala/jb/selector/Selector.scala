package jb.selector

import jb.util.Const._
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.feature.{ChiSqSelector, ChiSqSelectorModel}

object Selector {

  def select_chi_sq(nFeatures: Int): Estimator[ChiSqSelectorModel] = {
    new ChiSqSelector().
      setNumTopFeatures(nFeatures).
      setFeaturesCol(SPARSE_FEATURES).
      setLabelCol(LABEL).
      setOutputCol(FEATURES)
  }

}
