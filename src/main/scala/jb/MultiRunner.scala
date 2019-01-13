package jb

import jb.server.SparkEmbedded

object MultiRunner {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogWarn()
    val runner = new Runner(5, 2, Array(2, 6, 10, 14))
    val scores = runner.calculateMvIScores("A/bi")
    scores.foreach(i => print(i + "\n"))
  }

}
