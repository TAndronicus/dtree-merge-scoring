package jb.selector

import jb.util.Conf._
import org.apache.spark.ml.feature.ChiSqSelector
import org.apache.spark.sql.DataFrame

object Selector {

  def select_chi_sq(vectorizedInput: DataFrame): DataFrame = {
        val selector = new ChiSqSelector().
      setNumTopFeatures(2).
      setFeaturesCol(SPARSE_FEATURES).
      setLabelCol(LABEL).
      setOutputCol(FEATURES)

    selector.fit(vectorizedInput).transform(vectorizedInput).select(FEATURES, LABEL)
  }

}
