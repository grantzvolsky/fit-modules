package knapsack.solvers

import knapsack.Configuration

import scala.annotation.tailrec
import scala.collection.immutable

object DPByCapacity extends KnapsackSolver {
  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    val solutions = Array.fill(items.length, capacity + 1){0}

    for {
      col <- 0 to capacity
      row <- items.indices
      candidate = items(row)
    } {
      if (row >= 1) {
        if (col >= candidate._1) {
          val withCurrentItem = solutions(row-1)(col-candidate._1) + candidate._2
          val withoutCurrentItem = solutions(row-1)(col)
          solutions(row)(col) = Math.max(withCurrentItem, withoutCurrentItem)
        } else solutions(row)(col) = solutions(row-1)(col)
      } else solutions(row)(col) = if (col < candidate._1) 0 else candidate._2
    }

    @tailrec
    def getBestWeight(weight: Int): Int = {
      if (weight <= 0) return 0
      val lastItemIdx = items.length - 1
      if (solutions(lastItemIdx)(weight) > solutions(lastItemIdx)(weight - 1)) weight
      else getBestWeight(weight - 1)
    }

    Configuration(getBestWeight(capacity), solutions(items.length - 1)(capacity), immutable.BitSet.empty)
  }

  override def toString = "DPByCapacity"
}
