package knapsack.solvers

import scala.collection.immutable

object NaiveRecursionSansConfigVars extends KnapsackSolver {
  /**
    * Solve an instance of the knapsack problem using recursion, without keeping track of used items. Running time is O(2^n)
    *
    * @param items Array of (weight, value) tuples representing items.
    * @param capacity Knapsack capacity.
    * @return Configuration object which contains the best value and weight, but doesn't contain information about used items.
    */
  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    var bestW, bestV = 0

    def go(w: Int, v: Int, idx: Int): Unit = {
      if (v > bestV && w <= capacity) { bestV = v; bestW = bestW }
      if (idx == items.length) return
      go(w, v, idx + 1)
      go(w + items(idx)._1, v + items(idx)._2, idx + 1)
    }

    go(0, 0, 0)
    Configuration(bestW, bestV, immutable.BitSet.empty)
  }

  override def toString = "NaiveRecursionSansConfigVars"
}
