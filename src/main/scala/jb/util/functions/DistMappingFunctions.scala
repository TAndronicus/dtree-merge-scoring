package jb.util.functions

object DistMappingFunctions {

  val simpleMapping: Double => Double = (dist: Double) => math.exp(exponentialCoefficient(dist))
  val momentMappingFunction: (Double, Double) => Double = (distFromBorder: Double, distFromMoment: Double) =>.7 * simpleMapping(distFromBorder) +.3 * simpleMapping(distFromMoment)
  val parametrizedMomentMappingFunction: Double => (Double, Double) => Double = (alpha: Double) => (distFromBorder: Double, distFromMoment: Double) => alpha * simpleMapping(distFromBorder) + (1 - alpha) * simpleMapping(distFromMoment)

  private def exponentialCoefficient(dist: Double): Double = {
    math.pow(10 * dist - 3, 2) / -4
  }

}
