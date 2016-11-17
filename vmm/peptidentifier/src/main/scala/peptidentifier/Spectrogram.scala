package peptidentifier

import scala.collection.mutable

case class Spectrogram(peptide: List[AminoAcid],
                       pepmass: Double,
                       charge: String,
                       title: String,
                       peaks: List[(Double, Int)])

case object Spectrogram {
  val varPattern = raw"(.*)=(.*)".r
  val peakPattern = raw"(\d*\.?\d+) (\d+)".r

  def fromMgf(it: Iterator[String]): List[Spectrogram] = {
    var sgrams: List[Spectrogram] = Nil

    while (it.hasNext) {
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

      var peptide: List[AminoAcid] = Nil
      var pepmass: Double = 0
      var charge: String = ""
      var title: String = ""
      var peaks: List[(Double, Int)] = Nil

      sgLines foreach {
        case varPattern(lhs, rhs) => lhs match {
          case "PEPTIDE" => peptide = rhs.getBytes.flatMap(code => AminoAcid.get(code.toChar)) toList
          case "PEPMASS" => pepmass = rhs.toDouble
          case "CHARGE" => charge = rhs
          case "TITLE" => title = rhs
        }
        case peakPattern(mz, intensity) => peaks = (mz.toDouble, intensity.toInt) :: peaks
      }

      if (sgLines.nonEmpty) sgrams = Spectrogram(peptide, pepmass, charge, title, peaks) :: sgrams
    }
    sgrams
  }
}