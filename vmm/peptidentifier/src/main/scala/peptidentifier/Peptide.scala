package peptidentifier

import scala.collection.mutable

case class Peptide(acids: List[AminoAcid]) {
  override def toString = {
    val sb = new StringBuilder
    sb.append("seq\tB\tY\n")

    val yMass: mutable.Queue[Double] = mutable.Queue.empty
    var yMassAcc = 19.02322
    ((acids.length - 1) to 0 by -1) foreach { idx =>
      yMassAcc += acids(idx).mono
      yMass.enqueue(yMassAcc)
    }

    var bMassAcc = 1.00794
    acids foreach { a =>
      bMassAcc += a.mono
      sb.append(f"${a.code}\t$bMassAcc%.2f\t${yMass.dequeue()}%.2f\n")
    }

    sb.toString
  }
}

object Peptide {
  def fromString(str: String): Peptide = {
    Peptide(str.getBytes.flatMap(code => AminoAcid.get(code.toChar)) toList)
  }
}