package jb.parser

import jb.model._
import jb.util.Const.EPSILON
import org.apache.spark.ml.tree.{ContinuousSplit, InternalNode, Node}
import scala.util.control.Breaks._

class TreeParser(val weightAggregator: Array[Rect] => Double, rowWithin: (Array[Double], Array[Double]) => (Array[Double], Array[Double]) => Boolean) {

  def dt2rect(parent: Rect, node: Node): Array[Rect] = {
    node match {
      case _: InternalNode =>
        val interNode = node.asInstanceOf[InternalNode]

        val newMax = parent.max.clone()
        newMax(interNode.split.featureIndex) = interNode.split.asInstanceOf[ContinuousSplit].threshold
        val newMin = parent.min.clone()
        newMin(interNode.split.featureIndex) = interNode.split.asInstanceOf[ContinuousSplit].threshold

        val leftChild = parent.copy(max = newMax)
        val rightChild = parent.copy(min = newMin)

        dt2rect(leftChild, node.asInstanceOf[InternalNode].leftChild) ++ dt2rect(rightChild, node.asInstanceOf[InternalNode].rightChild)
      case _ =>
        Array(parent.copy(label = node.prediction))
    }
  }

  def calculateLabel(mins: Array[Double], maxes: Array[Double], rects: Array[Array[Rect]]): Double = {
    rects.map(
      geometricalRepresentation => geometricalRepresentation.filter(_.isWithin(rowWithin(mins, maxes))) // filtering ones that span the cube
        .groupBy(_.label)
        .mapValues(weightAggregator) // sum weights (volumes)
        .reduce((a1, a2) => if (a1._2 > a2._2) a1 else a2)._1 // choosing label with the greatest value
    ).groupBy(identity).reduce((l1, l2) => if (l1._2.length > l2._2.length) l1 else l2)._1 // chosing label with the greatest count
  }

  def rect2dt(mins: Array[Double], maxes: Array[Double], elSize: Array[Double], dim: Int, maxDim: Int, rects: Array[Array[Rect]]): SimpleNode = {
    var diff = maxes(dim) - mins(dim)
    if (diff > elSize(dim) + EPSILON) {
      val mid = mins(dim) + math.floor((diff + EPSILON) / (2 * elSize(dim))) * elSize(dim)
      val (newMins, newMaxes) = (mins.clone(), maxes.clone())
      newMins(dim) = mid
      newMaxes(dim) = mid
      InternalSimpleNode(rect2dt(mins, newMaxes, elSize, dim, maxDim, rects), rect2dt(newMins, maxes, elSize, dim, maxDim, rects), new SimpleSplit(dim, mid))
    } else if (dim < maxDim - 1) {
      val newDim = dim + 1
      diff = maxes(newDim) - mins(newDim)
      val mid = mins(newDim) + math.floor((diff + EPSILON) / (2 * elSize(newDim))) * elSize(newDim)
      val (newMins, newMaxes) = (mins.clone(), maxes.clone())
      newMins(newDim) = mid
      newMaxes(newDim) = mid
      InternalSimpleNode(rect2dt(mins, newMaxes, elSize, newDim, maxDim, rects), rect2dt(newMins, maxes, elSize, newDim, maxDim, rects), new SimpleSplit(newDim, mid))
    } else {
      LeafSimpleNode(calculateLabel(mins, maxes, rects))
    }
  }

  protected def areAdjacent(tuple: (Rect, Rect)): Boolean = {
    val (r1, r2) = tuple
      for (dim <- r1.min.indices) {
        if (math.max(r1.min(dim), r2.min(dim)) > math.min(r1.max(dim), r2.max(dim))) return false
      }
    true
  }

  protected def areOfSameClasses(tuple: (Rect, Rect)): Boolean = {
    tuple._1.label == tuple._2.label
  }

  protected def createEdge(tuple: (Rect, Rect)): Edge = {
    val (r1, r2) = tuple
    val (mins, maxes) = (new Array[Double](r1.min.length), new Array[Double](r1.min.length))
    for (dim <- r1.min.indices) {
      mins(dim) = math.max(r1.min(dim), r2.min(dim))
      maxes(dim) = math.min(r1.max(dim), r2.max(dim))
    }
    Edge(mins, maxes)
  }

  def rects2edges(rects: Array[Rect]): Array[Edge] = {
    val indices = for (i <- rects.indices; j <- rects.indices if i < j) yield (i, j)
    indices.map(tuple => (rects(tuple._1), rects(tuple._2))).filterNot(areOfSameClasses).filter(areAdjacent).map(createEdge).toArray
  }

}
