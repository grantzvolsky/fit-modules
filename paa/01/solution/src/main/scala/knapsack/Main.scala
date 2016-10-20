package knapsack

import scala.collection.immutable

object Main extends App {

  /**
    * Transform an argument list into a map of options.
    * Source: http://stackoverflow.com/questions/2315912/scala-best-way-to-parse-command-line-parameters-cli
    */
  def listToOptionMap(lst: List[String]): Map[Symbol, String] = {
    def nextOption(map : Map[Symbol, String], list: List[String]) : Map[Symbol, String] = {
      def isSwitch(s : String) = s(0) == '-'
      list match {
        case Nil => map
        case "--method" :: value :: tail => nextOption(map ++ Map('method -> value), tail)
        case "--itemCnt" :: value :: tail => nextOption(map ++ Map('itemCnt -> value), tail)
        case string :: opt2 :: tail if isSwitch(opt2) => nextOption(map ++ Map('infile -> string), list.tail)
        case string :: Nil =>  nextOption(map ++ Map('infile -> string), list.tail)
        case option :: tail => println("Unknown option "+option)
          sys.exit(1)
      }
    }

    nextOption(Map(), lst)
  }

  /**
    * Select algorithm.
    */
  def getSolver(methodStr: String): KnapsackSolver = {
    methodStr match {
      case "NaiveIteration" => NaiveIteration
      case "NaiveRecursionSansConfigVars" => NaiveRecursionSansConfigVars
      case "VWRatioHeuristic" => VWRatioHeuristic
      case default => NaiveRecursion
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
  def getReference(refLines: Iterator[String]): Iterator[Solution] = {
    val solutionRegex = raw"^(\d+) (\d+) (\d+)(.*)".r
    for {
      solutionRegex(id, n, v, config) <- refLines
    } yield Solution(id, n.toInt, Configuration(0, v.toInt, immutable.BitSet.empty))
  }

  def getRefFile(n: Int) = s"/var/my_root/repos/fit/pdd/01/output/reference/knap_$n.sol.dat"
  def getInFile(n: Int) = s"/var/my_root/repos/fit/pdd/01/input/knap_$n.inst.dat"

  def reference(n: Int): Iterator[Solution] =  getReference(scala.io.Source.fromFile(getRefFile(n)).getLines)
  def input(n: Int): Iterator[String] = scala.io.Source.fromFile(getInFile(n)).getLines

  val options = listToOptionMap(args.toList)
  val solver = getSolver(options('method))
  val itemCnt = options('itemCnt).toInt
  val in = input(itemCnt)
  val ref = reference(itemCnt)
  val res = Benchmark.runSingle(solver, in, ref, itemCnt)

  println(res)
}