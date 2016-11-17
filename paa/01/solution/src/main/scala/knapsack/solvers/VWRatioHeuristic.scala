package knapsack.solvers

import knapsack.Configuration

import scala.collection.mutable

object VWRatioHeuristic extends KnapsackSolver {
  /** Approximates a solution of the knapsack problem.
    *
    * This approach orders items based on their value/weight ratio and tries to feed them into the knapsack in this order.
    * If an item that is next in line doesn't fit, it is simply skipped.
    *
    * @note The time complexity of this approach is O(n*log n), where n is the number of items.
    *
    * @param items Array of (weight, value) tuples representing items.
    * @param capacity Knapsack capacity.
    * @return The best approximated configuration.
    */
  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    val sorted = items.zipWithIndex.sortWith((lhs, rhs) => (lhs._1._2 / lhs._1._1) > (rhs._1._2 / rhs._1._1)) // We need to add an index so that we know which item is which even after sorting them.
    var accW, accV = 0
    val configIndices = new mutable.BitSet
    sorted foreach { itm =>
      if (accW + itm._1._1 <= capacity) { // Add itm to the knapsack if there's enough capacity left.
        accW += itm._1._1
        accV += itm._1._2
        configIndices.add(itm._2)
      }
    }
    Configuration(accW, accV, configIndices)
  }

  override def toString = "VWRatioHeuristic"
}
