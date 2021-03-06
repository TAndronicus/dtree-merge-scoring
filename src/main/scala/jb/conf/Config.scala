package jb.conf

object Config {

  /** Models */
  val maxDepth: Int = 5
  val impurity = "gini"
  val datasetSize = 10000

  /** Result catcher */
  val treshold: Double = .4
  val batch: Int = 4
  val minIter: Int = 10
  val maxIter: Int = 200

  /** Other */
  val recalculate = false
  val joinTrainingAndValidationSets = true

}
