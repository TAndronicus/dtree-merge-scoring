package jb.selector

import jb.util.Const._
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.feature.{ChiSqSelector, ChiSqSelectorModel}

object Selector {

  def select_chi_sq(): Estimator[ChiSqSelectorModel] = {
    new ChiSqSelector().
      setNumTopFeatures(2).
      setFeaturesCol(SPARSE_FEATURES).
      setLabelCol(LABEL).
      setOutputCol(FEATURES)
  }

}
