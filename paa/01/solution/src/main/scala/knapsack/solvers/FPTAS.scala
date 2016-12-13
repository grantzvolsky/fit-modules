package knapsack.solvers

object FPTAS extends KnapsackSolver {

  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    val epsilon: Double = System.getProperty("epsilon").toDouble

    val maxValueItm: (Int, Int) = items.maxBy(itm => itm._2)
    val C_m = maxValueItm._2
    val K = (epsilon * C_m) / items.length

    def priceMagic(items: Array[(Int, Int)]): Array[(Int, Int)] = {
      items.map { itm =>
        val c_i = (itm._2 / K).toInt
        (itm._1, c_i)
      }
    }

    val config = DPByValue.solve(priceMagic(items), capacity)

    Configuration(config.weight, (config.value * K).toInt, config.indices)
  }

  override def toString = "FPTAS"
}
