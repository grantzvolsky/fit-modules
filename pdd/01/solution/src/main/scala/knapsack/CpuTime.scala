package knapsack

import java.lang.management.{ ManagementFactory, ThreadMXBean }

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
  def measureCpuTime[T](f: => T): (T, Double) = {
    val start = getCpuTime
    val r = f
    val end = getCpuTime
    val t = (end - start) / 1000000.0
    (r, t)
  }

  /**
    * The same as measureCpuTime but the function f is applied repeatedly
    * until a cumulative threshold time use is reached (currently 0.1 seconds).
    * The time returned is the cumulative time divided by the number of repetitions.
    * Therefore, better accuracy is obtained for very small run-times.
    * The function f should be side-effect free!
    */
  def measureCpuTimeRepeated[T](f: => T): (T, Double) = ??? // TODO, original implementation wasn't suitable
}