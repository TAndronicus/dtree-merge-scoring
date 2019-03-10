package jb

import jb.util.Const.FILENAME_PREFIX
import jb.util.result.{LeastBatchExhaustiveResultCatcher, ResultCatcher}

object MultiRunner {


  def run(nClassif: Int, nFeatures: Int, alpha: Double): Unit = {
    val filenames = Array("bi", "bu", "c", "d", "h", "i", "m", "p", "se", "t", "wd", "wi")

    val runner = new Runner(nClassif, nFeatures, alpha)
    val resultCatcher = runForFiles(runner)(filenames)

    resultCatcher.writeScores(Array(nClassif.toString, nFeatures.toString, alpha.toString))
  }

  private def runForFiles(runner: Runner)(filenames: Array[String]): ResultCatcher = {
    val resultCatcher = getResultCatcher
    while (resultCatcher.canConsume && !resultCatcher.isFull) {
      try {
        val scores = new Array[Array[Double]](filenames.length)
        for (index <- filenames.indices) {
          scores(index) = runner.calculateMvIScores(FILENAME_PREFIX + filenames(index))
        }
        resultCatcher.consume(scores)
      } catch {
        case e: Throwable => println("Caught " + e.getMessage)
      }
    }
    resultCatcher
  }

  private def getResultCatcher: ResultCatcher = {
    new LeastBatchExhaustiveResultCatcher(0.5, 10, 150, 350)
  }

}
