package jb.util.functions

object WithinDeterminers {

  def spansMid(elementaryCubeMin: Double, elementaryCubeMax: Double): (Double, Double) => Boolean = {
        val av = (elementaryCubeMin + elementaryCubeMax) / 2D
    (treeCubeMin: Double, treeCubeMax: Double) => av >= treeCubeMin && av <= treeCubeMax
  }

}
