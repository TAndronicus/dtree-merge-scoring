package jb.util.functions

object DistMappingFunctions {

  val simpleMapping: Double => Double = (dist: Double) => math.exp(exponentialCoefficient(dist))

  private def exponentialCoefficient(dist: Double): Double = {
    math.pow(dist - .3, 2) / -4
  }

  private val momentMapping: (Double, Double) => Double = (distFromBorder: Double, distFromMoment: Double) => math.exp(exponentialCoefficient(distFromBorder) + exponentialCoefficient(distFromMoment))

  val momentMappingFunction: (Double, Double) => Double = (distFromBorder: Double, distFromMoment: Double) => .7 * simpleMapping(distFromBorder) + .3 * momentMapping(distFromBorder, distFromMoment)

}
