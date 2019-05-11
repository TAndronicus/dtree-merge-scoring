package jb.model.dt

import jb.util.Const.FEATURES
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.linalg.{DenseVector, SparseVector}
import org.apache.spark.sql.DataFrame

class MomentIntegratedDecisionTreeModel(
                                         val baseModels: Array[DecisionTreeClassificationModel],
                                         val distMappingFunction: Double => Double,
                                         val moments: Map[Double, Array[Double]]
                                       )
  extends IntegratedDecisionTreeModel {

  override def transform(dataframe: DataFrame): Array[Double] = {
    dataframe.select(FEATURES).collect().map({ row =>
      row.toSeq.head match {
        case dense: DenseVector =>
          transform(dense.toArray)
        case sparse: SparseVector =>
          transform(sparse.toArray)
      }
    })
  }

  //TODO: optimize double prediction
  def transform(obj: Array[Double]): Double = {
    baseModels.indices.map(i => (baseModels(i).predict(new DenseVector(obj)), weightedDist(i, obj)))
      .groupBy(_._1)
      .mapValues(_.map(_._2).sum)
      .reduce((l1, l2) => if (l1._2 > l2._2) l1 else l2)._1
  }

  def weightedDist(index: Int, obj: Array[Double]): Double = {
    distMappingFunction(IntegratedDecisionTreeUtil.pointDist(moments(baseModels(index).predict(new DenseVector(obj))), obj))
  }

}
