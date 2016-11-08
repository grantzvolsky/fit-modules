package knapsack.solvers

import knapsack.Configuration

import scala.collection.mutable

object NaiveRecursion extends KnapsackSolver {
  /**
    * Solve an instance of the knapsack problem using recursion. Running time is O(2^n)
    *
    * @note Further performance optimalization can be achieved by replacing BitSet with an array of booleans.
    *
    * @param items Array of (weight, value) tuples representing items.
    * @param capacity Knapsack capacity.
    * @return The best configuration.
    */
  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    var best = Configuration.identity
    val configIndices: mutable.BitSet = mutable.BitSet.empty

    def go(w: Int, v: Int, idx: Int): Unit = {
      if (w > capacity) return
      if (v > best.value) best = Configuration(w, v, new mutable.BitSet(configIndices.toBitMask))
      if (idx == items.length) return
      go(w, v, idx + 1)
      configIndices.add(idx)
      go(w + items(idx)._1, v + items(idx)._2, idx + 1)
      configIndices.remove(idx)
    }

    go(0, 0, 0)
    best
  }

  override def toString = "NaiveRecursion"
}
