package jb.model.dt

import jb.model.Edge
import jb.util.Const.FEATURES
import jb.model.dt.IntegratedDecisionTreeUtil.{pointDist, edgeOvelaps}
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.linalg.{DenseVector, SparseVector}
import org.apache.spark.sql.DataFrame

class CombinedIntegratedDecisionTreeModel(
                                         val baseModels: Array[DecisionTreeClassificationModel],
                                         val distMappingFunction: (Double, Double) => Double,
                                         val edges: Array[Array[Edge]],
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

  // TODO: Check - distances to edges and moments should be independent
  // TODO: optimize double prediction
  def transform(obj: Array[Double]): Double = {
    edges.indices.map(i => (baseModels(i).predict(new DenseVector(obj)), weightedDist(i, obj))) // (label, weight)
      .groupBy(_._1)
      .mapValues(_.map(_._2).sum) // TODO
      .reduce((l1, l2) => if (l1._2 > l2._2) l1 else l2)._1
  }

  def weightedDist(index: Int, obj: Array[Double]): Double = {
    distMappingFunction(minDistUnsigned(index, obj), distFromMoment(index, obj))
  }

  def distFromMoment(index: Int, obj: Array[Double]): Double = {
    pointDist(obj, moments(baseModels(index).predict(new DenseVector(obj))))
  }

  def minDistUnsigned(index: Int, point: Array[Double]): Double = {
    if (edges(index).isEmpty) {
      return .5 // Data is normalized
    }
    edges(index).map(edge => distUnsigned(edge, point)).min
  }

  def distUnsigned(edge: Edge, point: Array[Double]): Double = {
    if (edgeOvelaps(edge, point, 0)) {
      math.abs(edge.min(1) - point(1))
    } else if (edgeOvelaps(edge, point, 1)) {
      math.abs(edge.min(0) - point(0))
    } else {
      math.min(pointDist(edge.min, point), pointDist(edge.max, point))
    }
  }

}
