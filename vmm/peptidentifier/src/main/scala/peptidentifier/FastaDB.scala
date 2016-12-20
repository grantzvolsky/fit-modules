package peptidentifier

import breeze.linalg.{SparseVector => SBV}

object FastaDB {
  val fastaLines = scala.io.Source.fromFile("/var/my_root/repos/fit/vmm/input/sequence_database/amop_msdb_10000.fasta").getLines
  val database = PeptideSequence.fromFasta(fastaLines)

  def query(query: Spectrogram, maxMassDiff: Double) = {
    val rhsBreeze: SBV[Double] = Spectrum.normalize(query.spectrum).peaks

    val realMass = query.pepmass.get * query.charge.get.charAt(0).toString.toInt
    val massFilter = (p: Peptide) => Math.abs(p.mass - realMass) < maxMassDiff
    val dbPeptides = database.flatMap(_.splice()).filter(massFilter)
    println(dbPeptides.size)
    val res = dbPeptides.flatMap { p =>
      //val lhsY: SBV[Double] = Spectrum.normalize(Spectrum.fromPeaks(p.yPeaks())).peaks
      val lhsB: SBV[Double] = Spectrum.normalize(Spectrum.fromPeaks(p.yPeaks())).peaks

      List(
        (breeze.linalg.functions.cosineDistance(lhsB, rhsBreeze), p)//,
        //(breeze.linalg.functions.cosineDistance(lhsY, rhsBreeze), p)
      )
    }.sortWith((l, r) => l._1 < r._1)

    res
  }
}
