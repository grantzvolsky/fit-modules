package peptidentifier

import scala.collection.mutable.ListBuffer

case class Peptide(acids: List[AminoAcid]) {
  private def peptideToSpectrum(acids: List[AminoAcid], acc: Double): List[Double] = {
    acids match {
      case Nil => Nil
      case acid :: tail => acc + acid.mono :: peptideToSpectrum(tail, acc + acid.mono)
      case acid :: Nil => acc + acid.mono :: Nil
    }
  }

  def bPeaks(): List[Double] = peptideToSpectrum(this.acids, 1.00794) // TODO: Jak by se tyto konstanty mely jmenovat?
  def yPeaks(): List[Double] = peptideToSpectrum(this.acids.reverse, 19.02322) // TODO: Proc se y ionty pocitaji odzadu? To je konvence?
  def mass: Double = acids.map(_.mono).sum + (2 * 19.0232)

  override def toString = {
    acids.mkString("")
  }

  def print = {
    val sb = new StringBuilder
    sb.append("seq\tB\tY\n")

    val b = bPeaks
    val y = yPeaks

    acids.indices foreach { i =>
      sb.append(f"${acids(i).code}\t${b(i)}%.2f\t${y(i)}%.2f\n")
    }

    sb.toString
  }
}


object Peptide {
  def fromString(str: String): Peptide = {
    Peptide(str.getBytes.flatMap(code => AminoAcid.get(code.toChar)) toList)
  }
}