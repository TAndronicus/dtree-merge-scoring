package jb

import jb.server.SparkEmbedded
import jb.util.Const.FILENAME_PREFIX

object MultiRunner {

  def main(args: Array[String]): Unit = {
    SparkEmbedded.setLogWarn()
    val reps = 10
    val filenames = Array("bi", "bu", "c", "d", "h", "i", "m", "p", "se", "so", "sp", "t", "wd", "wi")
    val runner = new Runner(5, 2, Array(2, 6, 10, 14))
    val finalScores = runForFiles(reps, runner)(filenames)
    finalScores.map(_ / reps).foreach(i => print(i + "\n"))
  }

  private def runForFiles(reps: Int, runner: Runner)(filenames: Array[String]) = {
    val finalScores = Array(0D, 0D, 0D, 0D, 0D)
    for (filename <- filenames) {
      val scores = runReps(reps, runner, filename)
      scores.indices.foreach(i => finalScores(i) += scores(i))
    }
    finalScores
  }

  private def runReps(reps: Int, runner: Runner, filename: String) = {
    val finalScores = Array(0D, 0D, 0D, 0D, 0D)
    for (_ <- 0.until(reps)) {
      val scores = runner.calculateMvIScores(FILENAME_PREFIX + filename)
      scores.indices.foreach(i => finalScores(i) += scores(i))
    }
    finalScores
  }
}
