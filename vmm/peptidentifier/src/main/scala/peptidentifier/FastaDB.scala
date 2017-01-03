package peptidentifier

import breeze.linalg.{SparseVector => SBV}

object FastaDB {
  val fastaLines: Iterator[String] = scala.io.Source.fromFile("/var/my_root/repos/fit/vmm/input/sequence_database/amop_msdb_10000.fasta").getLines
  val database: List[PeptideSequence] = PeptideSequence.fromFasta(fastaLines)

  @deprecated("This method implicitly uses ySpectrum.")
  def query(query: Spectrogram, maxMassDiff: Double) = {
    val rhsBreeze: SBV[Double] = Spectrum.normalize(query.spectrum).peaks

    val realMass = query.pepmass.get * query.charge.get.charAt(0).toString.toInt
    val massFilter = (p: Peptide) => Math.abs(p.mass - realMass) < maxMassDiff
    val dbPeptides = database.flatMap(_.splice()).filter(massFilter)
    println(dbPeptides.size)
    val res = dbPeptides.flatMap { p =>
      val lhs: SBV[Double] = Spectrum.discretize(Spectrum.fromPeaks(p.yPeaks()).peaks) // No need to normalise because generated peaks are normal

      List(
        (breeze.linalg.functions.cosineDistance(lhs, rhsBreeze), p)
      )
    }.sortWith((l, r) => l._1 < r._1)

    res
  }

  def customPeaksQuery(query: Spectrogram, maxMassDiff: Double, peptideToPeaks: Peptide => List[Double]): List[(Double, Peptide)] = {
    val rhsBreeze: SBV[Double] = Spectrum.normalize(query.spectrum).peaks

    val realMass = query.pepmass.get * query.charge.get.charAt(0).toString.toInt
    val massFilter = (p: Peptide) => Math.abs(p.mass - realMass) < maxMassDiff
    val dbPeptides = database.flatMap(_.splice()).filter(massFilter)
    println(dbPeptides.size)
    val res = dbPeptides.flatMap { p =>
      val lhs: SBV[Double] = Spectrum.discretize(Spectrum.fromPeaks(peptideToPeaks(p)).peaks) // No need to normalise because generated peaks are normal

      List((breeze.linalg.functions.cosineDistance(lhs, rhsBreeze), p))
    }.sortWith((l, r) => l._1 < r._1)

    res
  }
}
