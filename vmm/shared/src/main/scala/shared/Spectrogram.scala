package shared

import scala.collection.{breakOut, mutable}

case class Spectrogram(peptide: Option[Peptide],
                       pepmass: Option[Double],
                       charge: Option[String],
                       title: Option[String],
                       spectrum: Spectrum)

case object Spectrogram {
  val varPattern = raw"(.*)=(.*)".r
  val peakPattern = raw"(\d*\.?\d+) (\d+)".r

  def fromMgfIndexed(it: Iterator[String]): Map[String, Spectrogram] = {
    fromMgf(it).map(p => (p.peptide.get.toString, p))(breakOut)
  }

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