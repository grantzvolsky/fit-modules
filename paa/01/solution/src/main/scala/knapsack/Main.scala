package knapsack

import knapsack.benchmark.Benchmark
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
      //case default => NaiveRecursion
    }
  }

  /**
    * Parse reference output and return an iterator of [[Solution]]
    */
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

  def run(instances: Array[String]) = {
    solver.solveAll(instances) foreach println
  }

  if (System.getProperty("mode") == "print") run(io.Source.stdin.getLines().toArray)
  else if (System.getProperty("mode") == "benchmark") runBenchmark()
  else println("Unknown mode.")
}