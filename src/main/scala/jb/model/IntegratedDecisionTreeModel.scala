package jb.model

import jb.util.Const.FEATURES
import org.apache.spark.ml.linalg.{DenseVector, SparseVector}
import org.apache.spark.sql.DataFrame

class IntegratedDecisionTreeModel(val rootNode: SimpleNode) {

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
    traverseTree(rootNode, obj)
  }

  def traverseTree(node: SimpleNode, obj: Array[Double]): Double = {
    node match {
      case nodeL: LeafSimpleNode =>
        nodeL.label
      case nodeI: InternalSimpleNode =>
        val split = nodeI.split
        traverseTree(if (split.value > obj(split.featureIndex)) nodeI.leftChild else nodeI.rightChild, obj)
    }
  }


}
