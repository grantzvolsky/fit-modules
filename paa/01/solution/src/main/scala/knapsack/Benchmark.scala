package knapsack

import scala.concurrent.duration.Duration

object Benchmark {
  case class BenchResult(method: String, n: Int, time: Duration, avgRelErr: Double) {
    override def toString = method + " " + n + " " + time.toMicros + " " + avgRelErr
  }
  def runSingle(solver: KnapsackSolver, in: Iterator[String], ref: Iterator[Solution], itemCnt: Int): BenchResult = {
    val refValues = ref map (s => s.bestConfig.value) toList

    val res = CpuTime.measureCpuTime {
      Main.solve(solver, in) map (o => o.bestConfig.value) toList
    }

    val outValues = res._1
    val relErrors = (refValues, outValues).zipped.map((ref, out) => (ref - out).toDouble / ref)
    val avgRelErr = relErrors.sum / relErrors.size
    val avgTime = res._2 / outValues.size

    BenchResult(solver.toString, itemCnt, avgTime, avgRelErr)
  }

  def runAll(solver: KnapsackSolver, testScope: List[Int], in: Iterator[String], ref: Iterator[Solution], itemCnt: Int): List[BenchResult] = {
    testScope.map { n =>
      runSingle(solver, in, ref, n)
    } sortBy(b => b.n)
  }

  def formatResults(r: List[BenchResult]): String = {
    val sb = new StringBuilder
    r foreach(r => sb.append(r + "\n"))
    sb.toString
  }
}
