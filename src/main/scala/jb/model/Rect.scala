package jb.model

import java.util.stream.IntStream

case class Rect(var min: Array[Double], var max: Array[Double], var label: Double = 0D, var mid: Array[Double] = Array.emptyDoubleArray,
                var volume: Double = 0D) {

  def isWithin(mins: Array[Double], maxes: Array[Double]): Boolean = {
    if (mins.length != min.length) throw new RuntimeException("Sizes don't match")
    for (index <- mins.indices) {
      if (mins(index) > min(index) || maxes(index) < max(index)) return false
    }
    true
  }

  override def toString: String = "Min: " + min.map(item => item.toString).reduce((s1, s2) => s1 + ", " + s2) + ", Max: " +
    max.map(_.toString).reduce((s1, s2) => s1 + ", " + s2) + ", Label: " + label.toString + ", Mid: " +
    mid.map(_.toString).reduce((s1, s2) => s1 + ", " + s2) + ", Volume: " + volume.toString

}
