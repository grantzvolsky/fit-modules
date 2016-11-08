package knapsack.solvers

import knapsack.Configuration

import scala.annotation.tailrec
import scala.collection.immutable

object DPByValue extends KnapsackSolver {
  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    val totalValue = items.map(_._2).sum
    val solutions = Array.fill(totalValue + 1, items.length){0}

    for {
      row <- 0 to totalValue
      col <- items.indices
      candidate = items(col)
    } {
      val copy = if (col >= 1) solutions(row)(col-1) else 0
      val compose = candidate._2 match {
        case v if col >= 1 && row > v => if (solutions(row - v)(col - 1) > 0) solutions(row - v)(col - 1) + candidate._1 else 0
        case v if row == v => candidate._1
        case v => 0
      }

      solutions(row)(col) = if (copy != 0 && compose != 0) Math.min(copy, compose) else Math.max(copy, compose)
    }

    @tailrec
    def getBestValue(value: Int): Int = {
      if (value <= 0) return 0
      val lastItemIdx = items.length - 1
      val weight = solutions(value)(lastItemIdx)
      if (weight > 0 && weight <= capacity) value
      else getBestValue(value - 1)
    }

    Configuration(solutions(getBestValue(totalValue))(items.length - 1), getBestValue(totalValue), immutable.BitSet.empty)
  }

  override def toString = "DPByValue"
}
