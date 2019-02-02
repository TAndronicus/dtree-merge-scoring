package jb.model

import jb.util.Const.FEATURES
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.linalg.{DenseVector, SparseVector}
import org.apache.spark.sql.DataFrame

class IntegratedDecisionTreeModel(val edges: Array[Array[Edge]], val baseModels: Array[DecisionTreeClassificationModel]) {

  def transform(dataframe: DataFrame): Array[Double] = {
    dataframe.select(FEATURES).collect().map({ row =>
      row.toSeq.head match {
        case dense: DenseVector =>
          transform(dense.toArray)
        case sparse: SparseVector =>
          transform(sparse.toArray)
      }
    })
  }

  def pointDist(p1: Array[Double], p2: Array[Double]): Double = {
    math.sqrt(p1.indices.map(i => math.pow(p1(i) - p2(i), 2)).sum)
  }

  def distUnsigned(edge: Edge, obj: Array[Double]): Double = {
    if (edgeOvelaps(edge, obj, 0)) {
      math.abs(edge.min(1) - obj(1))
    } else if (edgeOvelaps(edge, obj, 1)) {
      math.abs(edge.min(0) - obj(0))
    } else {
      math.min(pointDist(edge.min, obj), pointDist(edge.max, obj))
    }
  }

  private def edgeOvelaps(edge: Edge, obj: Array[Double], dim: Int): Boolean = {
    edge.min(dim) <= obj(dim) && edge.max(dim) >= obj(dim) && edge.min(dim) != edge.max(dim)
  }

  def minDistUnsigned(edgeModel: Array[Edge], obj: Array[Double]): Double = {
    if (edgeModel.isEmpty) {
      return .5  // Data is normalized
    }
    edgeModel.map(edge => distUnsigned(edge, obj)).min
  }

  def transform(obj: Array[Double]): Double = {
    edges.indices.map(i => (baseModels(i).predict(new DenseVector(obj)), minDistUnsigned(edges(i), obj))).groupBy(_._1).mapValues(_.map(_._2).sum).reduce((l1, l2) => if (l1._2 > l2._2) l1 else l2)._1
  }

}
