package peptidentifier

import breeze.linalg.{VectorBuilder, SparseVector => SBV}

@deprecated("Use breezeImpl/Spectrum instead")
case class Spectrum(peaks: Map[Double, Int]) {
  def normalize = Spectrum.normalize(this)
}

@deprecated("Use breezeImpl/NormalizedSpectrum instead")
case class NormalizedSpectrum(peaks: SBV[Double])

case object Spectrum {
  val discreteAmplifier = 1
  val discreteBuckets = 5000 * discreteAmplifier

  def discretize(peaks: Map[Double, Int]): SBV[Double] = {
    val vb: VectorBuilder[Double] = new VectorBuilder(length = discreteBuckets)

    peaks foreach {case (mz, intensity) =>
      val pigeonHoleIdx = (mz * discreteAmplifier).toInt
      vb.add(pigeonHoleIdx, vb(pigeonHoleIdx) + intensity.toDouble)
    }

    vb.toSparseVector
  }

  def normalize(s: Spectrum): NormalizedSpectrum = {
    val res = discretize(s.peaks)

    val maxIntensity: Double = res.reduceLeft(_ max _)
    if (Math.abs(maxIntensity) > 1.0001) { // is not normalised
      res.activeKeysIterator foreach (idx => res(idx) = res(idx) / maxIntensity)
    }

    NormalizedSpectrum(res)
  }

  /*def cosim(l: NormalizedSpectrum, r: NormalizedSpectrum): Double = {
    if (l.peaks.size < 1 || r.peaks.size < 1) return 0
    def euclNorm(v: Iterable[Double]): Double = Math.sqrt(v.reduceLeft((acc, v) => acc + Math.pow(v, 2)))
    def euclDot(l: Iterable[Double], r: Iterable[Double]): Double = {
      val lIt = l.iterator
      val rIt = r.iterator
      (0 until Math.min(l.size, r.size)).foldLeft(0.0)((acc, idx) => acc + (lIt.next * rIt.next))
    }

    euclDot(l.peaks.values, r.peaks.values) / (euclNorm(l.peaks.values) * euclNorm(r.peaks.values))
  }*/

  def fromPeaks(peaks: List[Double]) = Spectrum(peaks zip Stream.continually(1) toMap)
}