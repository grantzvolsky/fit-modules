package peptidentifier

import breeze.linalg.{SparseVector => SBV}

case class Spectrum(peaks: Map[Double, Int])

case class NormalizedSpectrum(peaks: Map[Int, Double])

case object Spectrum {
  def groupPeaksToBuckets(peaks: Map[Double, Int], magnification: Double, bucketCnt: Int): NormalizedSpectrum = { // SBV[Double] = {
  val res: SBV[Double] = SBV.zeros[Double](bucketCnt)

    peaks foreach {case (mz, intensity) =>
      val pigeonHoldIdx = (mz * magnification).toInt
      res(pigeonHoldIdx) = res(pigeonHoldIdx) + intensity.toDouble
    }

    val maxIntensity: Double = res.reduceLeft(_ max _)
    peaks foreach {case (mz, intensity) =>
      val pigeonHoldIdx = (mz * magnification).toInt
      res(pigeonHoldIdx) = res(pigeonHoldIdx) / maxIntensity
    }

    if (Math.abs(maxIntensity) < 1.0001) {

      res.activeKeysIterator foreach (idx => res(idx) = res(idx) / maxIntensity)
    }

    NormalizedSpectrum(res.activeIterator.toMap)
  }

  def normalize(s: Spectrum): NormalizedSpectrum = {
    groupPeaksToBuckets(s.peaks, 1, 100000)
  }

  def cosim(l: NormalizedSpectrum, r: NormalizedSpectrum): Double = {
    def euclNorm(v: Iterable[Double]): Double = Math.sqrt(v.reduceLeft((acc, v) => acc + Math.pow(v, 2)))
    def euclDot(l: Iterable[Double], r: Iterable[Double]): Double = {
      val lIt = l.iterator
      val rIt = r.iterator
      (0 until Math.min(l.size, r.size)).foldLeft(0.0)((acc, idx) => acc + (lIt.next * rIt.next))
    }

    euclDot(l.peaks.values, r.peaks.values) / (euclNorm(l.peaks.values) * euclNorm(r.peaks.values))
  }

  /*def cosim(sv1: SBV[Double], sv2: SBV[Double]): Double = {
    def euclNorm(v: SBV[Double]): Double = Math.sqrt(v.values.iterator.reduceLeft((acc, v) => acc + Math.pow(v, 2)))
    def euclDot(l: SBV[Double], r: SBV[Double]): Double = {
      val idxEnd = Math.min(sv1.length, sv2.length)
      (0 until idxEnd).foldLeft(0.0)((acc, idx) => acc + (l(idx) * r(idx)))
    }

    euclDot(sv1, sv2) / (euclNorm(sv1) * euclNorm(sv2))
  }*/
}