import java.io.{BufferedWriter, File, FileWriter}

import collection.mutable.{HashMap, MultiMap, Set}
import scala.io.Source

object sort extends App{
  val cntList = Source.fromFile("countris.csv").getLines().toList
  val map  = new  HashMap[Int, Set[String]] with  MultiMap[Int,  String]
  for(a <- cntList)
    map.addBinding( a.split(", ")(0).toInt, a.split(", ")(1) )


  val keys  = mergeSort[Int](map.keys.toList)(_<_)
  val f = new ((Int, Int) => Int) { def apply(x: Int, y: Int): Int = x+y }
  List(f).map(keys.reduce).foreach(println)
//  println Function
  val result = Map.empty
  val file = new File("sorted.csv")
  val bw = new BufferedWriter(new FileWriter(file))
  for(k <- keys) {
    val list = map(k).toList
    mergeSort(list)(_.compare(_) < 0).foreach( entry => {
      bw.write(k + ", " +entry)
      bw.newLine()
    })
  }
  bw.close()

  def mergeSort[T]( xs: List[ T])(f:(T,T)=>Boolean) : List[T] = {
    val n = xs.length / 2
    if (n == 0) xs
    else {
      def merge(xs: List[ T], ys: List[ T]): List[T] =
        (xs, ys) match {
          case(Nil, ys) => ys
          case(xs, Nil) => xs
          case(x :: xs1, y :: ys1) =>
            if (f(x, y)) x::merge(xs1, ys)
            else y :: merge(xs, ys1)
        }
      val (left, right) = xs splitAt(n)
      merge(mergeSort(left)(f), mergeSort(right)(f))
    }
  }
  def quickSort(list: ListBuffer [(Int,_)]):List[(Int,_)] = list match {
    case ListBuffer() => list.toList
    case _ => {
      val mid = list.remove(0)
      val leftRight = list.partition(_._1 < mid._1)
//      if(  Runtime.getRuntime.freeMemory()/ (1024*1024)<80  )
//        println("**---- Free Memory:  " + Runtime.getRuntime.freeMemory() / (1024*1024))
      (quickSort(leftRight._1) :+ mid) ++ quickSort(leftRight._2)
    }
  }
}
