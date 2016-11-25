package peptidentifier

import breeze.linalg.{SparseVector => SBV}

import scala.collection.mutable

case class Spectrum(peaks: Map[Double, Int])

case class RelativeAbundanceSpectrum(peaks: Map[Int, Double])

case object Spectrum {
  def groupPeaksToBuckets(peaks: Map[Double, Int], magnification: Double, bucketCnt: Int): RelativeAbundanceSpectrum = { // SBV[Double] = {
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

    RelativeAbundanceSpectrum(res.activeIterator.toMap)
  }

  def clearNoise(s: Spectrum): RelativeAbundanceSpectrum = {
    groupPeaksToBuckets(s.peaks, 1, 100000)
  }

  def cosim(l: RelativeAbundanceSpectrum, r: RelativeAbundanceSpectrum): Double = {
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

case class Spectrogram(peptide: Option[Peptide],
                       pepmass: Option[Double],
                       charge: Option[String],
                       title: Option[String],
                       spectrum: Spectrum)

case object Spectrogram {
  val varPattern = raw"(.*)=(.*)".r
  val peakPattern = raw"(\d*\.?\d+) (\d+)".r

  def fromMgf(it: Iterator[String]): List[Spectrogram] = {
    var sgrams: List[Spectrogram] = Nil

    def readRawSpectrogram(it: Iterator[String]) = {
      val sgLines: mutable.Queue[String] = mutable.Queue.empty

      var line: String = it.next()
      while (line != "BEGIN IONS" && it.hasNext) {
        line = it.next()
      }
      line = if (it.hasNext) it.next() else "END IONS"
      while (line != "END IONS" && it.hasNext) {
        sgLines.enqueue(line)
        line = it.next()
      }

      sgLines
    }

    while (it.hasNext) {
      val sgLines = readRawSpectrogram(it)

      var peptideOpt: Option[Peptide] = None
      var pepmass: Option[Double] = None
      var charge: Option[String] = None
      var title: Option[String] = None
      val peaks: mutable.Map[Double, Int] = mutable.Map[Double, Int]()

      sgLines foreach {
        case varPattern(lhs, rhs) => lhs match {
          case "PEPTIDE" => peptideOpt = Some(Peptide.fromString(rhs))
          case "PEPMASS" => pepmass = Some(rhs.toDouble)
          case "CHARGE" => charge = Some(rhs)
          case "TITLE" => title = Some(rhs)
        }
        case peakPattern(mz, intensity) => peaks(mz.toDouble) = intensity.toInt // peaks = (mz.toDouble, intensity.toInt) :: peaks
      }

      if (sgLines.nonEmpty) sgrams = Spectrogram(peptideOpt, pepmass, charge, title, Spectrum(peaks.toMap)) :: sgrams
    }
    sgrams
  }
}