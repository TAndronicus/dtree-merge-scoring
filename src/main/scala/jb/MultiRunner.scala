package jb

import jb.server.SparkEmbedded

object MultiRunner {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogWarn()
    val reps = 20
    val runner = new Runner(5, 2, Array(2, 6, 10, 14))
    var finalScores = Array(0D, 0D, 0D, 0D, 0D)
    for (_ <- 0.until(reps)) {
      val scores = runner.calculateMvIScores("A/so")
      scores.indices.foreach(i => finalScores(i) += scores(i))
    }
    finalScores.map(_/reps).foreach(i => print(i + "\n"))
  }

}
