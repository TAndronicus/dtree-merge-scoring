package jb.parser

import jb.model.Rect
import org.apache.spark.ml.tree.{ContinuousSplit, InternalNode, Node}

object TreeParser {

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
        val vol = parent.min.indices.map(i => parent.max(i) - parent.min(i)).product
        Array(parent.copy(label = node.prediction, volume = vol))
    }
  }

}
