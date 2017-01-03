package peptidentifier

import scala.collection.mutable.ListBuffer

case class PeptideSequence(description: String, data: String) {
  /**
    * https://en.wikipedia.org/wiki/Protein_splicing
    * Vlastně se jedná o obrácený problém batohu. Předměty, které dáváme do batohu, jsou aminokyseliny, podle kterých
    * sekvenci rozdělujeme. Potřebujeme vyčíst všechny možnosti, jak 2...
    * Řešení:
    * 1) vyčít indexy odstraňovaných inteinů
    * 2) Vyčíslit všechny podmnožiny
    */
  def splice(): List[Peptide] = {
    val separatorIds: ListBuffer[Int] = ListBuffer(-1) // -1 will be treated as the first separator
    for (i <- 0 until data.length) {
      if (data(i) == 'K' || ((i+1 != data.length) && data(i) == 'R' && data(i+1) != 'P')) {
        separatorIds.append(i)
      }
    }

    val peptides: ListBuffer[String] = ListBuffer.empty

    for (i <- separatorIds.indices) {

      separatorIds.length - 1 match {
        case last if i+1 == last => peptides.append(data.substring(separatorIds(i)+1, separatorIds(i+1)+1))
        case last if i+2 <= last =>
          peptides.append(data.substring(separatorIds(i)+1, separatorIds(i+1)+1))
          peptides.append(data.substring(separatorIds(i)+1, separatorIds(i+2)+1))
        case default =>
      }
    }

    if (separatorIds.length >= 2 && separatorIds(separatorIds.length - 2)+1 < data.length) {
      peptides ++= List(// We can't add data.length as the last separator because separatorIds(i+1) would fail. Instead we do the last bits manually.
        data.substring(separatorIds(separatorIds.length - 2) + 1, data.length),
        data.substring(separatorIds.last + 1, data.length)
      )
    }

    peptides map Peptide.fromString toList
  }
}

case object PeptideSequence {
  def fromFasta(it: Iterator[String]): List[PeptideSequence] = {
    val res: ListBuffer[PeptideSequence] = ListBuffer.empty

    var description: String = ""
    while(it.hasNext) {
      while (it.hasNext && !description.startsWith(">")) {
        description = it.next()
      }

      var sequenceLine: String = ""
      val seq: ListBuffer[String] = ListBuffer.empty
      while (it.hasNext && !sequenceLine.startsWith(">")) {
        seq.append(sequenceLine)
        sequenceLine = it.next()
      }
      res.append(PeptideSequence(description, seq.mkString))
      description = sequenceLine
    }
    res.toList
  }
}