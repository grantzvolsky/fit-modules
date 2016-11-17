package knapsack

import knapsack.solvers.NaiveRecursion
import org.scalatest._

class KnapsackSpec extends FlatSpec {
  "Naive Recursion" must "find the optimal solution" in {
    val res = Benchmark.runAll(NaiveRecursion, List(10, 15, 20))
    assert(res.map(_.avgRelErr).sum == 0)
  }
}
