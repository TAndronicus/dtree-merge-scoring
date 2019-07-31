package jb

import jb.model.{Coefficients, MappingModel}
import jb.util.Const.FILENAME_PREFIX
import jb.util.result.{LeastBatchExhaustiveResultCatcher, ResultCatcher}

object MultiRunner {


  def run(nClassif: Int, nFeatures: Int, coefficients: Coefficients, mappingModel: MappingModel): Unit = {
    coefficients.validate()
    val filenames = Array("bi", "bu", "c", "d", "h", "i", "m", "p", "se", "t", "wd", "wi")

    val runner = new Runner(nClassif, nFeatures, coefficients, mappingModel)
    val resultCatcher = runForFiles(runner)(filenames)

    resultCatcher.writeScores(Array(nClassif.toString, coefficients.getAllCoefficients.mkString("_")))
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
    new LeastBatchExhaustiveResultCatcher(0.4, 10, 150, 350)
  }

}
