package jb

import jb.server.SparkEmbedded

import scala.concurrent.Future

object Playground {

  def asyncCall(element: String): Future[String] = ???

  def main(args: Array[String]): Unit = {
    import SparkEmbedded.ss.implicits._
    val df = Seq(("one", 1324235345435.4546)).toDF("a", "b")
    df.write.mode("append").insertInto("test")
    SparkEmbedded.ss.sql("select * from test").show(false)
    val a = Array(Array(1))
    a.filter(!_.isEmpty)
  }

}
