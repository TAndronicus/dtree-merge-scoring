package jb

object Playground {

  def main(args: Array[String]): Unit = {
    val a = for (i <- 0 until 5; j <- 0 until 5 if i != j) yield (i, j)
    println(a)
  }

}
