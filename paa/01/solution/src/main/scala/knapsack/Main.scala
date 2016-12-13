package knapsack

import knapsack.benchmark.{Benchmark, KnapGenerator}
import knapsack.benchmark.Benchmark.BenchResult
import knapsack.solvers._

import scala.annotation.tailrec
import scala.collection.immutable

object Main extends App {

  /**
    * Select algorithm.
    */
  def solver: KnapsackSolver = {
    System.getProperty("method") match {
      case "NaiveRecursion" => NaiveRecursion
      case "NaiveIteration" => NaiveIteration
      case "NaiveRecursionSansConfigVars" => NaiveRecursionSansConfigVars
      case "VWRatioHeuristic" => VWRatioHeuristic
      case "BranchAndBound" => BranchAndBound
      case "DPByCapacity" => DPByCapacity
      case "DPByValue" => DPByValue
      case "FPTAS" => FPTAS
      case "Evolution" => Evolution
      //case default => NaiveRecursion
    }
  }

  def runBenchmark() = {
    def getReference(refLines: Array[String]): Array[Solution] = {
      val solutionRegex = raw"^(\d+) (\d+) (\d+)(.*)".r
      for {
        solutionRegex(id, n, v, config) <- refLines
      } yield Solution(id, n.toInt, Configuration(0, v.toInt, immutable.BitSet.empty))
    }

    def getRefFile(n: Int) = s"/var/my_root/repos/fit/paa/01/output/reference/knap_$n.sol.dat"
    def getInFile(n: Int) = s"/var/my_root/repos/fit/paa/01/input/knap_$n.inst.dat"

    def reference(n: Int): Array[Solution] = getReference(scala.io.Source.fromFile(getRefFile(n)).getLines.toArray)
    def input(n: Int): Array[String] = scala.io.Source.fromFile(getInFile(n)).getLines.toArray

    val itemCnt = System.getProperty("itemCnt").toInt
    val in = input(itemCnt)
    val ref = reference(itemCnt)
    val res = Benchmark.runSingleBatch(solver, in, ref, itemCnt)

    println(res)
  }

  def runRandomBenchmark() = {
    val itemCnt = System.getProperty("itemCnt").toInt
    val batchSize = System.getProperty("batchSize").toInt
    val capacityPerItmWSum = 0.25
    val maxW = System.getProperty("maxW").toInt
    val maxV = System.getProperty("maxV").toInt
    val wDistribution = 1.0
    val wDistributionMode = 0
    val generator = KnapGenerator(itemCnt, batchSize, capacityPerItmWSum, maxW, maxV, wDistribution, wDistributionMode)

    val problems: Array[String] = generator()
    val referenceSolutions = DPByCapacity.solveAll(problems)
    def outputFormat = (r: BenchResult) => s"$itemCnt,${r.method},${r.time.toMicros},${"%1.20f".format(r.maxRelErr)},${"%1.20f".format(r.avgRelErr)}"
    val res = Benchmark.runSingleBatch(solver, problems, referenceSolutions, itemCnt)
    println(res)
  }

  def run(instances: Array[String]) = {
    solver.solveAll(instances) foreach println
  }

  if (System.getProperty("mode") == "print") run(io.Source.stdin.getLines().toArray)
  else if (System.getProperty("mode") == "benchmark") runRandomBenchmark()
  else println("Unknown mode.")
}