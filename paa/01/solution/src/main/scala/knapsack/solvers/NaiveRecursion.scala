package knapsack.solvers

import scala.collection.mutable

object NaiveRecursion extends KnapsackSolver {

  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    var best = Configuration.identity
    val configIndices: mutable.BitSet = mutable.BitSet.empty

    def go(w: Int, v: Int, idx: Int): Unit = {
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
