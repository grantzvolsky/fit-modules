package peptidentifier

case class Peptide(acids: List[AminoAcid]) {
  def peptideToSpectrum(acids: List[AminoAcid], acc: Double): List[Double] = {
    acids match {
      case Nil => Nil
      case acid :: tail => acc + acid.mono :: peptideToSpectrum(tail, acc + acid.mono)
      case acid :: Nil => acc + acid.mono :: Nil
    }
  }

  def bSpectrum(): List[Double] = peptideToSpectrum(this.acids, 1.00794) // TODO: Jak by se tyto konstanty mely jmenovat?
  def ySpectrum(): List[Double] = peptideToSpectrum(this.acids.reverse, 19.02322) // TODO: Proc se y ionty pocitaji odzadu? To je konvence?

  override def toString = {
    val sb = new StringBuilder
    sb.append("seq\tB\tY\n")

    val bPeaks = bSpectrum()
    val yPeaks = ySpectrum()

    acids.indices foreach { i =>
      sb.append(f"${acids(i).code}\t${bPeaks(i)}%.2f\t${yPeaks(i)}%.2f\n")
    }

    sb.toString
  }
}

object Peptide {
  def fromString(str: String): Peptide = {
    Peptide(str.getBytes.flatMap(code => AminoAcid.get(code.toChar)) toList)
  }
}