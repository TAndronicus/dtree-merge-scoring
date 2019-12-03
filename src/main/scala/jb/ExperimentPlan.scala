package jb

import jb.model.{Coefficients, PreTraining}
import jb.server.SparkEmbedded

object ExperimentPlan {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogError()
    val nClassifs = Array(3, 5, 7, 9)
    val nFeatures = 2
    val alphas = Array(0, .2, .4, .6, .8, 1)
    val betas = Array(0, .1, .2, .3, .4, .5)
    val gammas = Array(5, 8, 11, 14, 17, 20)
//    val (beta1, beta2) = (.5, 0)
//    val (gamma1, gamma2) = (20, 5)
    for (nC <- nClassifs; alpha <- alphas; beta1 <- betas; beta2 <- betas; gamma1 <- gammas; gamma2 <- gammas) {
      val coeffs = Coefficients(alpha, beta1, beta2, gamma1, gamma2)
      println(nC + " classifiers, " + coeffs)
      MultiRunner.run(nC, nFeatures, coeffs, PreTraining())
    }
  }

}
