package knapsack.benchmark

import knapsack.solvers.{KnapsackSolver, Solution}

import scala.concurrent.duration.Duration

object Benchmark {
  def eps = System.getProperty("eps").toDouble
  case class BenchResult(method: String, n: Int, time: Duration, avgRelErr: Double, maxRelErr: Double) {
    override def toString = s"$method $n ${time.toMicros} ${"%1.20f".format(avgRelErr)} ${"%1.20f".format(maxRelErr)} ${"%1.20f".format(eps)}"
  }
  def runSingleBatch(solver: KnapsackSolver, in: Array[String], ref: Array[Solution], itemCnt: Int): BenchResult = {
    val refValues = ref map (s => s.bestConfig.value) toList

    val res = CpuTime.measureCpuTime {
      solver.solveAll(in) map (o => o.bestConfig.value) toList
    }

    val outValues = res._1
    val relErrors = (refValues, outValues).zipped.map((ref, out) => (ref - out).toDouble / ref)
    val avgRelErr = relErrors.sum / relErrors.size
    val maxRelErr = relErrors.max
    val avgTime = res._2 / outValues.size

    BenchResult(solver.toString, itemCnt, avgTime, avgRelErr, maxRelErr)
  }

  def runAll(solver: KnapsackSolver, testScope: List[Int], in: Array[String], ref: Array[Solution], itemCnt: Int): List[BenchResult] = {
    testScope.map { n =>
      runSingleBatch(solver, in, ref, n)
    } sortBy(b => b.n)
  }

  def formatResults(r: List[BenchResult]): String = {
    val sb = new StringBuilder
    r foreach(r => sb.append(r + "\n"))
    sb.toString
  }
}
