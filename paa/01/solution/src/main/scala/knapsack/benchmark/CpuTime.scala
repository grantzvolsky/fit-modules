package knapsack.benchmark

import java.lang.management.{ManagementFactory, ThreadMXBean}

import scala.concurrent.duration.{Duration, _}

/**
  * Created by troll on 03/12/16.
  */
package object CpuTime {
  val bean: ThreadMXBean = ManagementFactory.getThreadMXBean
  def getCpuTime = if (bean.isCurrentThreadCpuTimeSupported) bean.getCurrentThreadCpuTime else 0
  /**
    * Runs the argument function f and measures the user+system time spent in it in seconds.
    * Accuracy depends on the system, preferably not used for runs taking less than 0.1 seconds.
    * Returns a pair consisting of
    * - the return value of the function call and
    * - the time spent in executing the function.
    */
  def measureCpuTime[T](f: => T): (T, Duration) = {
    val start = getCpuTime
    val r = f
    val end = getCpuTime
    val t = (end - start) / 1000.0
    if (t < 1000000) {
      val avgT = measureCpuTimeRepeated(f)
      (r, Duration(avgT / 1000.0, MICROSECONDS))
    } else (r, Duration(t, MICROSECONDS))
  }

  /**
    * The same as measureCpuTime but the function f is applied repeatedly
    * until a cumulative threshold time use is reached (currently 0.1 seconds).
    * The time returned is the cumulative time divided by the number of repetitions.
    * Therefore, better accuracy is obtained for very small run-times.
    * The function f should be side-effect free!
    */
  def measureCpuTimeRepeated[T](f: => T): Double = {
    var runs = -2
    var start, end = 0L
    while (end - start < 70000000L) {
      if (runs == 0) start = getCpuTime
      f
      if (runs >= 0) end = getCpuTime
      runs += 1
    }
    (end - start) / runs
  }
}
