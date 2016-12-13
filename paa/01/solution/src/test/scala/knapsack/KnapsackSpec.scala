package knapsack

import java.io.{File, PrintWriter}

import knapsack.benchmark.Benchmark.BenchResult
import knapsack.benchmark.{Benchmark, KnapGenerator}
import knapsack.solvers._
import org.scalatest._

class KnapsackSpec extends FlatSpec {
  val itemCnt = 200
  val batchSize = 2
  val capacityPerItmWSum = 0.5
  val maxW = 500
  val maxV = 1000
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
      val referenceSolutions = DPByCapacity.solveAll(problems)
      def outputFormat = (r: BenchResult) => s"$itemCnt,$v,$maxV,$capacityPerItmWSum,$wDistribution,${r.method},${r.time.toMicros},${"%1.20f".format(r.maxRelErr)},${"%1.20f".format(r.avgRelErr)}"
      Benchmark.multiAlgorithmBenchmark(outputFormat, problems, referenceSolutions, itemCnt)
    }
  }


  def evolutionBenchmark[T](variable: IndexedSeq[T], parametricSolver: T => KnapsackSolver): IndexedSeq[String] = {
    variable map { v =>
      val in: Array[String] = generator()
      val ref = DPByCapacity.solveAll(in)
      def outputFormat = (r: BenchResult) => s"$itemCnt,$v,$maxV,$capacityPerItmWSum,$wDistribution,${r.method},${r.time.toMicros},${"%1.20f".format(r.maxRelErr)},${"%1.20f".format(r.avgRelErr)}"
      outputFormat(Benchmark.runSingleBatch(parametricSolver(v), in, ref, itemCnt))
    }
  }

  "Branch and bound" should "usually be faster than DP" in { // TODO
    val batchResult = evolutionBenchmark(1 to 75 by 5, Evolution.variableMaxGenerations)

    writeResults("/var/my_root/repos/fit/paa/01/report/gnuplot/task_3/variableMaxW.csv", batchResult.toIterator)

    assert(1 < 2)
  }
}
