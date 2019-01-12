package jb.model

import jb.util.Const.{FEATURES, LABEL}
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.DataFrame

class IntegratedDecisionTreeModel(val rootNode: SimpleNode) {

  def transform(dataframe: DataFrame): Array[Double] = {
    dataframe.select(FEATURES).collect().map(row => transform(row.toSeq.head.asInstanceOf[DenseVector].toArray))
  }

  def transform(obj: Array[Double]): Double = {
    traverseTree(rootNode, obj)
  }

  def traverseTree(node: SimpleNode, obj: Array[Double]): Double = {
    node match {
      case nodeL: LeafSimpleNode =>
        nodeL.label
      case _ =>
        val castedNode = node.asInstanceOf[InternalSimpleNode]
        val split = castedNode.split
        traverseTree(if (split.value > obj(split.featureIndex)) castedNode.leftChild else castedNode.rightChild, obj)
    }
  }


}
