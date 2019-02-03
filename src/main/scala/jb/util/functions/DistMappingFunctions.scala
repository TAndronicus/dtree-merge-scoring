package jb.util.functions

object DistMappingFunctions {

  val simpleMapping: Double => Double = (dist: Double) => math.exp(math.pow(dist - .3, 2) / -4)

}
