package knapsack

import scala.collection.BitSet

case class Configuration(weight: Int, value: Int, indices: BitSet)
case object Configuration {
  final val identity = Configuration(0, 0, BitSet.empty)
}