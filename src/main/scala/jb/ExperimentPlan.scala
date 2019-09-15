package jb

import jb.model.{Coefficients, PreTraining}
import jb.server.SparkEmbedded

object ExperimentPlan {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogError()
    val nClassifs = Array(3, 5, 7, 9)
    val nFeatures = 2
    val alphas = Array(0, .3, .7, 1)
    val (beta1, beta2) = (.5, 0)
    val (gamma1, gamma2) = (20, 5)
    for (nC <- nClassifs; alpha <- alphas) {
      val coeffs = Coefficients(alpha, beta1, beta2, gamma1, gamma2)
      MultiRunner.run(nC, nFeatures, coeffs, PreTraining())
    }
  }

}
