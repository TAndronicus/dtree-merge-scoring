package jb

import jb.server.SparkEmbedded

object ExperimentPlan {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogError()
    val nClassifs = Array(3, 5, 7, 9)
    val nFeatures = 2
    val alphas = Array(.3)
    for (nC <- nClassifs; alpha <- alphas) {
      MultiRunner.run(nC, nFeatures, alpha)
    }
  }

}
