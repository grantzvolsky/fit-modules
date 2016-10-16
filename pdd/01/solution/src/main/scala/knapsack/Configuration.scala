package knapsack

import scala.collection.mutable.BitSet

case class Configuration(weight: Int, value: Int, indices: BitSet)
case object Configuration {
  final val identity = Configuration(0, 0, BitSet.fromBitMask(Array(0)))
}