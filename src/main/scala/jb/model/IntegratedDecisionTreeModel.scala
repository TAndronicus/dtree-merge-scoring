package jb.model

import org.apache.spark.ml.classification.ClassificationModel
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.param.ParamMap

class IntegratedDecisionTreeModel(val rootNode: SimpleNode) extends ClassificationModel[Vector, IntegratedDecisionTreeModel] {

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
  override def numClasses: Int = throw new Exception("not yet impl")

  override protected def predictRaw(features: Vector): Vector = throw new Exception("not yet impl")

  override def copy(extra: ParamMap): IntegratedDecisionTreeModel = throw new Exception("Not yet implemented")

  override val uid: String = ""
}
