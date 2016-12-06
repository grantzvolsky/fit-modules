package knapsack.solvers

case class Solution(problemIdStr: String, itemCnt: Int, bestConfig: Configuration) {
  override def toString = {
    val nextIdx = bestConfig.indices.iterator.buffered
    val sb = new StringBuilder
    (0 until itemCnt) foreach { i =>
      val bit = if (nextIdx.hasNext && nextIdx.head == i) {nextIdx.next; 1} else 0
      sb.append(" " + bit)
    }

    problemIdStr + " " + itemCnt + " " + bestConfig.value + " " + sb.toString
  }
}
