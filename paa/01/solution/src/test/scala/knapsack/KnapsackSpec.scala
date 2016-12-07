package knapsack

import java.io.{File, PrintWriter}

import knapsack.benchmark.Benchmark.BenchResult
import knapsack.benchmark.{Benchmark, KnapGenerator}
import knapsack.solvers._
import org.scalatest._

class KnapsackSpec extends FlatSpec {
  val itemCnt = 15
  val batchSize = 15
  val capacityPerItmWSum = 0.5
  val maxW = 100
  val maxV = 250
  val wDistribution = 0.5
  val wDistributionMode = 0
  val generator = KnapGenerator(itemCnt, batchSize, capacityPerItmWSum, maxW, maxV, wDistribution, wDistributionMode)

  def writeResults(path: String, results: Iterator[String]) = {
    val pw = new PrintWriter(new File(path))
    //pw.write("n,maxW,maxV,capacityPerItmWSum,granularity,solver,time,maxRelErr,avgRelErr\n")
    results foreach (r => pw.write(r + '\n'))
    pw.close()
  }

  def parametricBenchmark[T](variable: IndexedSeq[T], inputGenerator: T => Array[String]): IndexedSeq[String] = {
    variable flatMap { v =>
      val problems: Array[String] = inputGenerator(v)
      val referenceSolutions = BranchAndBound.solveAll(problems)
      def outputFormat = (r: BenchResult) => s"$itemCnt,$v,$maxV,$capacityPerItmWSum,$wDistribution,${r.method},${r.time.toMicros},${"%1.20f".format(r.maxRelErr)},${"%1.20f".format(r.avgRelErr)}"
      Benchmark.multiAlgorithmBenchmark(outputFormat, problems, referenceSolutions, itemCnt)
    }
  }

  "Branch and bound" should "usually be faster than DP" in { // TODO
    val batchResult = parametricBenchmark(50 to 250 by 10, generator.variableMaxV)

    writeResults("/var/my_root/repos/fit/paa/01/report/gnuplot/task_3/variableMaxW.csv", batchResult.toIterator)

    assert(1 < 2)
  }
}
