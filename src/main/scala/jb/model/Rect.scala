package jb.model

case class Rect(var min: Array[Double], var max: Array[Double], var label: Double = 0D) {
  override def toString: String = "Min: " + min.map(item => item.toString).reduce((s1, s2) => s1 + ", " + s2) + ", Max: " +
    max.map(item => item.toString).reduce((s1, s2) => s1 + ", " + s2) + ", Label: " + label.toString
}
