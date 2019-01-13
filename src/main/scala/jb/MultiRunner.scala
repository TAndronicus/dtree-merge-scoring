package jb

import jb.server.SparkEmbedded

object MultiRunner {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogWarn()
    val runner = new Runner(5, 2, 5)
    val scores = runner.calculateMvIScores("A/biodeg.csv")
    scores.foreach(i => print(i + "\n"))
  }

}
