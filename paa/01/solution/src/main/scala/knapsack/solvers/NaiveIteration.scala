package knapsack.solvers

import scala.collection.mutable

object NaiveIteration extends KnapsackSolver {
  /** Solves an instance of the knapsack problem by iterating over all possible item combinations.
    *
    * Assume that there are 3 items in total. Any combination of these items is a configuration of the knapsack problem.
    * Example configurations: (0, 0, 0) - no items are selected. (1, 0, 0) - only the last item is selected.
    * The code below makes advantage of the fact that binary representation of an integer is the same as a configuration.
    * Iteration over integers from 0 to 2^n - 1 is analogous to iteration over all possible configurations of n binary
    * configuration variables.
    *
    * @note The time complexity of this approach is Theta(n*2^n), where n is the number of items. The aforementioned 2^n
    *       is multiplied by n because the evalIndices has the amortized cost of O(n).
    *
    * @param items Array of (weight, value) tuples representing items.
    * @param capacity Knapsack capacity.
    * @return The best configuration of the instance.
    */
  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    /**
      * @param indices Indices of items present in a configuration.
      * @return Problem configuration that contains precalculated total weight and value.
      */
    def evalIndices(indices: mutable.BitSet): Configuration = {
      var accW, accV = 0
      indices foreach {idx => // collection.BitSetLike optimises foreach for performance
        accW += items(idx)._1
        if (accW > capacity) return Configuration.identity
        accV += items(idx)._2
      }
      Configuration(accW, accV, indices)
    }

    var best = Configuration.identity
    (0 until 1 << items.length) foreach { configNo =>
      val configIndices = scala.collection.mutable.BitSet.fromBitMask(Array(configNo)) // BitSet representation of an integer.
      val config = evalIndices(configIndices)
      if (config.weight <= capacity && config.value > best.value) best = config
    }

    best
  }

  override def toString = "NaiveIteration"
}
