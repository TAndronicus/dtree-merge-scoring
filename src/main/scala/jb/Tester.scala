package jb

import jb.util.Const.{LABEL, PREDICTION}
import org.apache.spark.sql.DataFrame

object Tester {

  def testMvAcc(testedSubset: DataFrame, nClassif: Int): Double = {
    val cols = for (i <- 0.until(nClassif)) yield PREDICTION + "_" + i
    val mvLabels = testedSubset.select(cols.head, cols.takeRight(cols.length - 1): _*).collect()
      .map(row => row.toSeq.groupBy(_.asInstanceOf[Double].doubleValue()).mapValues(_.length).reduce((t1, t2) => if (t1._2 > t2._2) t1 else t2)).map(_._1)
    val refLabels = testedSubset.select(LABEL).collect().map(_.getInt(0).toDouble)
    val matched = mvLabels.indices.map(i => mvLabels(i) == refLabels(i))
    matched.count(i => i).toDouble / matched.length
  }

  def testIAcc(predictions: Array[Double], testSubset: DataFrame): Double = {
    val refLabels = testSubset.select(LABEL).collect().map(_.getInt(0).toDouble)
    val matches = refLabels.indices.map(i => predictions(i) == refLabels(i))
    matches.count(i => i).toDouble / matches.length
  }

}
