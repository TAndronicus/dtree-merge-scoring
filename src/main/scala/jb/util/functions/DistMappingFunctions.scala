package jb.util.functions

object DistMappingFunctions {

  val simpleMapping: Double => Double = (dist: Double) => math.exp(exponentialCoefficient(dist))

  private def exponentialCoefficient(dist: Double): Double = {
    math.pow(10 * dist - 3, 2) / -4
  }

  private val momentMapping: (Double, Double) => Double = (distFromBorder: Double, distFromMoment: Double) => math.exp(exponentialCoefficient(distFromBorder) + exponentialCoefficient(distFromMoment))

  val momentMappingFunction: (Double, Double) => Double = (distFromBorder: Double, distFromMoment: Double) => .7 * simpleMapping(distFromBorder) + .3 * momentMapping(distFromBorder, distFromMoment)

  val parametrizedMomentMappingFunction: Double => (Double, Double) => Double = (alpha: Double) => (distFromBorder: Double, distFromMoment: Double) => alpha * simpleMapping(distFromBorder) + (1 - alpha) * momentMapping(distFromBorder, distFromMoment)

}
