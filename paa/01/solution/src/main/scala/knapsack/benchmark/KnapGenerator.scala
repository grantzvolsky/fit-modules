package knapsack.benchmark

import sys.process._

case class KnapGenerator(n: Int, N: Int, m: Double, W: Int, C: Int, k: Double, d: Int) {
  def instanceBatchGenerator(itemsPerInst: Int = n,
                             instCnt: Int = N,
                             capacityPerItmWSum: Double = m,
                             maxItmW: Int = W,
                             maxItmV: Int = C,
                             wDistribution: Double = k,
                             wDistributionMode: Int = d): Array[String] = {
    val knapgenPath = System.getProperty("knapgenPath")

    val instances: String = s"""$knapgenPath -n $itemsPerInst -N $instCnt -m $capacityPerItmWSum""" +
      s""" -W $maxItmW -C $maxItmV -k $wDistribution -d $wDistributionMode""" !!

    println(s"""$knapgenPath -n $itemsPerInst -N $instCnt -m $capacityPerItmWSum""" +
      s""" -W $maxItmW -C $maxItmV -k $wDistribution -d $wDistributionMode""")

    instances.split('\n')
  }

  def const = (ignored: Int) => instanceBatchGenerator()
  def variableItemsPerInst = (n: Int) => instanceBatchGenerator(itemsPerInst = n)
  def variableMaxW = (W: Int) => instanceBatchGenerator(maxItmW = W)
  def variableMaxV = (C: Int) => instanceBatchGenerator(maxItmV = C)
  def variableCapacityPerItmWSum = (m: Double) => instanceBatchGenerator(capacityPerItmWSum = m)
  def variableWDistribution = (d: Int) => (k: Double) => instanceBatchGenerator(wDistribution = k, wDistributionMode = d)

  def apply() = instanceBatchGenerator()
}