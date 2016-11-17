package knapsack

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
    * Parse input and solve all problem instances using the selected algorithm.
    * Lines of input are in the following: [problemId] [n] [knapsackCapacity] [weight1] [value1] .. [weightN] [valueN]
    */
  def solve(solver: KnapsackSolver, instances: Iterator[String]): Iterator[Solution] = {
    val knapsackInstanceRegex = raw"^(\d+) (\d+) (\d+)(.*)".r
    for {
      knapsackInstanceRegex(problemIdStr, itemCntStr, knapsackCapacityStr, itemsStr) <- instances
      items = itemsStr.trim.split(" ").grouped(2) map (s => Tuple2(s(0).toInt, s(1).toInt)) toArray
    } yield Solution(problemIdStr, itemCntStr.toInt, solver.solve(items, knapsackCapacityStr.toInt))
  }

  /**
    * Parse reference output and return an iterator of [[Solution]]
    */
  def runBenchmark() = {
    def getReference(refLines: Iterator[String]): Iterator[Solution] = {
      val solutionRegex = raw"^(\d+) (\d+) (\d+)(.*)".r
      for {
        solutionRegex(id, n, v, config) <- refLines
      } yield Solution(id, n.toInt, Configuration(0, v.toInt, immutable.BitSet.empty))
    }

    def getRefFile(n: Int) = s"/var/my_root/repos/fit/paa/01/output/reference/knap_$n.sol.dat"
    def getInFile(n: Int) = s"/var/my_root/repos/fit/paa/01/input/knap_$n.inst.dat"

    def reference(n: Int): Iterator[Solution] =  getReference(scala.io.Source.fromFile(getRefFile(n)).getLines)
    def input(n: Int): Iterator[String] = scala.io.Source.fromFile(getInFile(n)).getLines

    val itemCnt = System.getProperty("itemCnt").toInt
    val in = input(itemCnt)
    val ref = reference(itemCnt)
    val res = Benchmark.runSingle(solver, in, ref, itemCnt, System.getProperty("epsilon").toDouble)

    println(res)
  }

  def run() = {
    val instances = io.Source.stdin.getLines()
    solve(solver, instances) foreach println
  }

  if (System.getProperty("mode") == "print") run()
  else if (System.getProperty("mode") == "benchmark") runBenchmark()
  else println("Unknown mode.")
}