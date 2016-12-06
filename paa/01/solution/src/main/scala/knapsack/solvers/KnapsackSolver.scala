package knapsack.solvers

trait KnapsackSolver {
  def solve(items: Array[(Int, Int)], capacity: Int): Configuration

  /**
    * @param instances Instance strings (lines of input) are in the following format:
    *                  [problemId] [n] [knapsackCapacity] [weight1] [value1] .. [weightN] [valueN]
    */
  def solveAll(instances: Array[String]): Array[Solution] = {
    val knapsackInstanceRegex = raw"^(\d+) (\d+) (\d+)(.*)".r
    for {
      knapsackInstanceRegex(problemIdStr, itemCntStr, knapsackCapacityStr, itemsStr) <- instances
      items = itemsStr.trim.split(" ").grouped(2) map (s => Tuple2(s(0).toInt, s(1).toInt)) toArray
    } yield Solution(problemIdStr, itemCntStr.toInt, this.solve(items, knapsackCapacityStr.toInt))
  }

  def toString: String
}
