package peptidentifier

import breeze.linalg.{SparseVector => SBV}

@deprecated("Use breezeImpl/Spectrum instead")
case class Spectrum(peaks: Map[Double, Int]) {
  def normalize = Spectrum.normalize(this)
}

@deprecated("Use breezeImpl/NormalizedSpectrum instead")
case class NormalizedSpectrum(peaks: Map[Int, Double])

case object Spectrum {
  def discretize(peaks: Map[Double, Int], magnification: Double, bucketCnt: Int): SBV[Double] = {
    val res: SBV[Double] = SBV.zeros[Double](bucketCnt)

    peaks foreach {case (mz, intensity) =>
      val pigeonHoldIdx = (mz * magnification).toInt
      res(pigeonHoldIdx) = res(pigeonHoldIdx) + intensity.toDouble
    }

    res
  }

  def normalize(s: Spectrum): NormalizedSpectrum = {
    val res = discretize(s.peaks, 1, 100000)

    val maxIntensity: Double = res.reduceLeft(_ max _)
    if (Math.abs(maxIntensity) > 1.0001) { // is not normalised
      res.activeKeysIterator foreach (idx => res(idx) = res(idx) / maxIntensity)
    }

    NormalizedSpectrum(res.activeIterator.toMap)
  }

  def cosim(l: NormalizedSpectrum, r: NormalizedSpectrum): Double = {
    if (l.peaks.size < 1 || r.peaks.size < 1) return 0
    def euclNorm(v: Iterable[Double]): Double = Math.sqrt(v.reduceLeft((acc, v) => acc + Math.pow(v, 2)))
    def euclDot(l: Iterable[Double], r: Iterable[Double]): Double = {
      val lIt = l.iterator
      val rIt = r.iterator
      (0 until Math.min(l.size, r.size)).foldLeft(0.0)((acc, idx) => acc + (lIt.next * rIt.next))
    }

    euclDot(l.peaks.values, r.peaks.values) / (euclNorm(l.peaks.values) * euclNorm(r.peaks.values))
  }

  def fromPeaks(peaks: List[Double]) = Spectrum(peaks zip Stream.continually(1) toMap)
}