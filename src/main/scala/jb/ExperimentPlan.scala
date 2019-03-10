package jb

import jb.server.SparkEmbedded

object ExperimentPlan {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogError()
    val nClassifs = Array(3, 5, 7)
    val nFeatures = 2
    val alpha = .3
    for (nC <- nClassifs) {
      MultiRunner.run(nC, nFeatures, alpha)
    }
  }

}
