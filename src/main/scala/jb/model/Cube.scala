package jb.model

case class Cube(var min: Array[Double], var max: Array[Double], var label: Double = 0D) {

  def mid: Array[Double] = {
    min.indices.map(i => (max(i) + min(i)) / 2D).toArray
  }

  def volume: Double = {
    min.indices.map(i => max(i) - min(i)).product
  }

  def isWithin(mins: Array[Double], maxes: Array[Double], rowWithin: (Double, Double) => (Double, Double) => Boolean): Boolean = {
    if (mins.length != min.length) throw new RuntimeException("Sizes don't match")
    for (index <- mins.indices) {
      if (!rowWithin(mins(index), maxes(index))(min(index), max(index))) return false
    }
    true
  }

  override def toString: String = "Min: " + min.map(item => item.toString).reduce((s1, s2) => s1 + ", " + s2) + ", Max: " +
    max.map(_.toString).reduce((s1, s2) => s1 + ", " + s2) + ", Label: " + label.toString + ", Mid: " +
    mid.map(_.toString).reduce((s1, s2) => s1 + ", " + s2) + ", Volume: " + volume.toString

}
