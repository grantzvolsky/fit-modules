package knapsack.solvers

import knapsack.Configuration

import scala.collection.immutable

object FPTAS extends KnapsackSolver {
  val epsilon = 0.001

  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    def epsilon = 0.001

    def priceMagic(items: Array[(Int, Int)]): Array[(Int, Int)] = {
      val maxValueItm: (Int, Int) = items.maxBy(itm => itm._2)
      val C_m = maxValueItm._2
      val K = (epsilon * C_m) / items.length

      items.map { itm =>
        val c_i = (itm._2 / K).toInt
        (itm._1, c_i)
      }
    }

    // TODO DP by value

    Configuration(0, 0, immutable.BitSet.empty)
  }

  override def toString = "FPTAS"
}
