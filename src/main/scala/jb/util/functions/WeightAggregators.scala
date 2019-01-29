package jb.util.functions

import jb.model.Rect

object WeightAggregators {

  val sumOfVolumes: Array[Rect] => Double = (ar: Array[Rect]) => ar.map(_.volume).sum

  val sumOfVolumesInv: Array[Rect] => Double = (ar: Array[Rect]) => ar.map(el => 1 / el.volume).sum

}
