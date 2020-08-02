package jb

import java.io.File

import jb.conf.Config
import jb.model.{Coefficients, PostTrainingTrainFiltered}
import jb.server.SparkEmbedded
import jb.util.Const

object ExperimentPlan {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogError()
    val nClassifs = Array(3, 5, 7, 9)
    val nFeatures = 2
    val alphas = Array(0, .3, .7, 1)
    val betas = Array(0, .5)
    val gammas = Array(5, 20)

    val calculated = getAlreadyCalculated
    for (nC <- nClassifs; alpha <- alphas; beta1 <- betas; beta2 <- betas; gamma1 <- gammas; gamma2 <- gammas) {
      val coeffs = Coefficients(alpha, beta1, beta2, gamma1, gamma2)
      if (Config.recalculate || !calculated.contains(coeffs)) {
        MultiRunner.run(nC, nFeatures, coeffs, PostTrainingTrainFiltered())
      } else {
        println("Already calculated: " + nC + " classifiers, " + coeffs)
      }
    }
  }

  def getAlreadyCalculated: Array[Coefficients] = {
    new File(Const.RESULT_PREFIX)
      .list()
      .map(_.split("_").tail)
      .map(_.map(_.toDouble))
      .map(Coefficients.fromArray(_: _*))
  }

}
