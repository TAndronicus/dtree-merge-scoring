package jb.model

class Coefficients(
                    val alpha: Double,
                    val beta1: Double,
                    val beta2: Double,
                    val gamma1: Double,
                    val gamma2: Double
                  ) {

  def validate(): Unit = {
    require(alpha >= 0)
    require(alpha <= 1)
    require(beta1 >= 0)
    require(beta1 <= 1)
    require(beta2 >= 0)
    require(beta2 <= 1)
    require(gamma1 >= 1)
    require(gamma2 >= 1)
  }

  def getBeta: Double = if (alpha == 0) beta1 else if (alpha == 1) beta2 else throw new RuntimeException("Ambiguous coefficient")

  def getGamma: Double = if (alpha == 0) gamma1 else if (alpha == 1) gamma2 else throw new RuntimeException("Ambiguous coefficient")

  def edgeDependent = alpha != 0

  def onlyEdgeDependent = alpha == 1

  def momentDependent = alpha != 1

  def onlyMomentDependent = alpha == 0

  def getAllCoefficients = Array(alpha, beta1, beta2, gamma1, gamma2)

}

object Coefficients {

  def apply(
             alpha: Double,
             beta1: Double,
             beta2: Double,
             gamma1: Double,
             gamma2: Double
           ): Coefficients = new Coefficients(
    alpha,
    beta1,
    beta2,
    gamma1,
    gamma2
  )

}
