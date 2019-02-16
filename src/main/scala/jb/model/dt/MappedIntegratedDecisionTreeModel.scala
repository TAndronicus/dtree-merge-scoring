package jb.model.dt

import jb.model.Edge
import jb.util.Const.FEATURES
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.linalg.{DenseVector, SparseVector}
import org.apache.spark.sql.DataFrame

class MappedIntegratedDecisionTreeModel(val edges: Array[Array[Edge]], val baseModels: Array[DecisionTreeClassificationModel], val moments: Map[Double, Array[Double]], val distMappingFunction: (Double, Double) => Double) {

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

  def transform(obj: Array[Double]): Double = {
    edges.indices.map(i => (predictLabel(obj, baseModels(i)), weightedDist(i, obj)))
      .groupBy(_._1)
      .mapValues(_.map(_._2).sum)
      .reduce((l1, l2) => if (l1._2 > l2._2) l1 else l2)._1
  }

  private def predictLabel(obj: Array[Double], baseModel: DecisionTreeClassificationModel): Double = {
    baseModel.predict(new DenseVector(obj))
  }

  def distFromMoment(index: Int, obj: Array[Double]): Double = {
    pointDist(obj, moments(predictLabel(obj, baseModels(index))))
  }

  def weightedDist(index: Int, obj: Array[Double]): Double = {
    distMappingFunction(minDistUnsigned(edges(index), obj), distFromMoment(index, obj))
  }

  def minDistUnsigned(edgeModel: Array[Edge], obj: Array[Double]): Double = {
    if (edgeModel.isEmpty) {
      return .5 // Data is normalized
    }
    edgeModel.map(edge => distUnsigned(edge, obj)).min
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

  def pointDist(p1: Array[Double], p2: Array[Double]): Double = {
    math.sqrt(p1.indices.map(i => math.pow(p1(i) - p2(i), 2)).sum)
  }

  private def edgeOvelaps(edge: Edge, obj: Array[Double], dim: Int): Boolean = {
    edge.min(dim) <= obj(dim) && edge.max(dim) >= obj(dim) && edge.min(dim) != edge.max(dim)
  }

}