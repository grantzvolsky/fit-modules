package knapsack

import scala.collection.mutable

object Main extends App {
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
  def solveInstanceNaiveIteration(items: Array[(Int, Int)], capacity: Int): Configuration = {
    /**
      * @param indices Indices of items present in a configuration.
      * @return A tuple containing the sum of weights and values in the configuration, or zeroes if total weight exceeds knapsack capacity.
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
  def solveInstanceWithVWRatioHeuristic(items: Array[(Int, Int)], capacity: Int): Configuration = {
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

  def solveInstanceNaiveRecursion(items: Array[(Int, Int)], capacity: Int): Configuration = {
    var best = Configuration.identity

    def go(w: Int, v: Int, idx: Int, configIndices: mutable.BitSet): Unit = {
      if (w > capacity) return
      if (v > best.value) best = Configuration(w, v, configIndices)
      if (idx == items.length) return
      go(w + items(idx)._1, v + items(idx)._2, idx + 1, configIndices + idx)
      go(w, v, idx + 1, configIndices)
    }

    go(0, 0, 0, new mutable.BitSet)
    best
  }

  /**
    * Select algorithm.
    */
  def getSolver: (Array[(Int, Int)], Int) => Configuration = {
    args.headOption match {
      case Some("-h") => {
        //println("Using value/weight heuristic.")
        solveInstanceWithVWRatioHeuristic
      }
      case default => {
        //println("Using the naive approach.")
        solveInstanceNaiveRecursion
      }
    }
  }

  /**
    * Parse input from stdin and solve all problem instances using the selected algorithm.
    * Each input row has the following format: [problemId] [n] [knapsackCapacity] [weight1] [value1] .. [weightN] [valueN]
    */
  val solver = getSolver
  val knapsackInstanceRegex = raw"^(\d+) (\d+) (\d+)(.*)".r
  for {
    knapsackInstanceRegex(problemIdStr, itemCntStr, knapsackCapacityStr, itemsStr) <- io.Source.stdin.getLines
    items = itemsStr.trim.split(" ").grouped(2) map (s => Tuple2(s(0).toInt, s(1).toInt)) toArray
  } println(Solution(problemIdStr, itemCntStr.toInt, solver(items, knapsackCapacityStr.toInt)))
}