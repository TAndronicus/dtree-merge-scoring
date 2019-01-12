package jb.model

import org.apache.spark.ml.classification.{ClassificationModel, ProbabilisticClassificationModel}
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.param.ParamMap

class IntegratedDecisionTreeModel(val rootNode: SimpleNode) {

//  override def predict(testData: Vector): Double = {
//    traverseTree(rootNode, testData)
//  }
//
//  def traverseTree(node: SimpleNode, testData: Vector): Double = {
//    node match {
//      case _: InternalSimpleNode =>
//        val internalNode = node.asInstanceOf[InternalSimpleNode]
//        traverseTree(if (internalNode.split.value > testData(internalNode.split.featureIndex)) internalNode.leftChild else internalNode.rightChild, testData)
//      case _: LeafSimpleNode =>
//        node.asInstanceOf[LeafSimpleNode].label
//    }
//  }
}
