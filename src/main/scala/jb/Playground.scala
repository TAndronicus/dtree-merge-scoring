package jb

object Playground {

  def main(args: Array[String]): Unit = {
    val a = Array(1, 2)
    val b = Array(6, 14)
    val res = math.sqrt(a.indices.map(i => math.pow(a(i) - b(i), 2)).sum)
    println(res)

  }

}
