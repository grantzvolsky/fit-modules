package knapsack

trait KnapsackSolver {
  def solve(items: Array[(Int, Int)], capacity: Int): Configuration
  def toString: String
}
