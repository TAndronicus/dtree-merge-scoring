package jb.util.functions

import jb.model.Cube

object WeightAggregators {

  val sumOfVolumes: Array[Cube] => Double = (ar: Array[Cube]) => ar.map(_.volume).sum

  val sumOfVolumesInv: Array[Cube] => Double = (ar: Array[Cube]) => ar.map(el => 1 / el.volume).sum

}
