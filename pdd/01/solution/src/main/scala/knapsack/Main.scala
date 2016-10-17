package knapsack

object Main extends App {
  /**
    * Select algorithm.
    */
  def getSolver: KnapsackSolver = {
    args.headOption match {
      case Some("--naiveiteration") => NaiveIteration
      case Some("-h") => VWRatioHeuristic
      case default => NaiveRecursion
    }
  }

  /**
    * Parse input from stdin and solve all problem instances using the selected algorithm.
    * Lines of input are in the following: [problemId] [n] [knapsackCapacity] [weight1] [value1] .. [weightN] [valueN]
    */
  val solver = getSolver
  val knapsackInstanceRegex = raw"^(\d+) (\d+) (\d+)(.*)".r
  for {
    knapsackInstanceRegex(problemIdStr, itemCntStr, knapsackCapacityStr, itemsStr) <- io.Source.stdin.getLines
    items = itemsStr.trim.split(" ").grouped(2) map (s => Tuple2(s(0).toInt, s(1).toInt)) toArray
  } println(Solution(problemIdStr, itemCntStr.toInt, solver.solve(items, knapsackCapacityStr.toInt)))

  println("[method " + solver.toString + "]")
}