package knapsack

import java.io.{File, PrintWriter}

import knapsack.benchmark.Benchmark.BenchResult
import knapsack.benchmark.{Benchmark, KnapGenerator}
import knapsack.solvers._
import org.scalatest._

class KnapsackSpec extends FlatSpec {
  val itemCnt = 15
  val batchSize = 50
  val capacityPerItmWSum = 0.5
  val maxW = 100
  val maxV = 250
  val wDistribution = 0.5
  val wDistributionMode = 0
  val generator = KnapGenerator(itemCnt, batchSize, capacityPerItmWSum, maxW, maxV, wDistribution, wDistributionMode)

  def runBenchmarks(p: BenchResult => String, in: Array[String], ref: Array[Solution], itemCnt: Int): List[String] = {
    List(
      p(Benchmark.runSingleBatch(NaiveRecursionSansConfigVars, in, ref, itemCnt)),
      p(Benchmark.runSingleBatch(BranchAndBound, in, ref, itemCnt)),
      p(Benchmark.runSingleBatch(DPByCapacity, in, ref, itemCnt)),
      p(Benchmark.runSingleBatch(VWRatioHeuristic, in, ref, itemCnt))
    )
  }

  def writeResults(path: String, results: Iterator[String]) = {
    val pw = new PrintWriter(new File(path))
    pw.write("n,maxW,maxV,capacityPerItmWSum,granularity,solver,time,maxRelErr,avgRelErr\n")
    results foreach (r => pw.write(r + '\n'))
    pw.close()
  }

  "Branch and bound" should "usually be faster than DP" in {
    val instBatchSrc: Array[String] = generator.variableMaxW(50)

    val batchResult = (50 to 250 by 10) flatMap { tmpMaxW =>
      val instBatch = instBatchSrc.clone
      //val instBatch: Array[String] = generator.variableMaxW(tmpMaxW)
      val refSolutions = DPByCapacity.solveAll(instBatch)
      def format = (r: BenchResult) => s"$itemCnt,$tmpMaxW,$maxV,$capacityPerItmWSum,$wDistribution,${r.method},${r.time.toMicros},${"%1.20f".format(r.maxRelErr)},${"%1.20f".format(r.avgRelErr)}"
      runBenchmarks(format, instBatch, refSolutions, itemCnt)
    }

    writeResults("/var/my_root/repos/fit/paa/01/report/gnuplot/task_3/variableMaxW.csv", batchResult.toIterator)

    assert(1 < 2)
  }
}
