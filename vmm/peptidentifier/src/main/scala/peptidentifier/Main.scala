package peptidentifier

import breeze.linalg.{SparseVector => SBV}


object Main extends App {

  val mgfLines = scala.io.Source.fromFile("/var/my_root/repos/fit/vmm/input/spectra/amethyst_annotated.mgf").getLines
  val queries = Spectrogram.fromMgfIndexed(mgfLines)

  val fastaLines = scala.io.Source.fromFile("/var/my_root/repos/fit/vmm/input/sequence_database/amop_msdb_10000.fasta").getLines
  val database = PeptideSequence.fromFasta(fastaLines)

  //println(Peptide.fromString("LGEHNIDVLEGNEQFINAAK").mass)

  testQuery2

  def testQuery1 = {
    val query = queries("DGDKPEETQGK")
    val rhs = Spectrum.normalize(query.spectrum)
    val lhsPeptide = Peptide.fromString("EVCASCHSLSR")
    //val lhsPeptide = Peptide.fromString("DGDKPEETQGK")
    val lhs = Spectrum.normalize(Spectrum.fromPeaks(lhsPeptide.yPeaks()))

    val lhsBreeze: SBV[Double] = new SBV[Double](Array.empty, Array.empty, 10000)
    val rhsBreeze: SBV[Double] = new SBV[Double](Array.empty, Array.empty, 10000)

    lhs.peaks foreach { p => lhsBreeze(p._1) = p._2 }
    rhs.peaks foreach { p => rhsBreeze(p._1) = p._2 }

    val cosim = breeze.linalg.functions.cosineDistance(lhsBreeze, rhsBreeze)

    val realQueryMass = query.pepmass.get * query.charge.get.charAt(0).toString.toInt
    val massDiff = Math.abs(realQueryMass - lhsPeptide.mass)

    println(lhsBreeze)
    println(rhsBreeze)
    println(s"cosim: $cosim, massdiff: $massDiff")
  }

  def testQuery2 = {
    val query = queries("DGDKPEETQGK")
    val rhs = Spectrum.normalize(query.spectrum)
    val rhsBreeze: SBV[Double] = new SBV[Double](Array.empty, Array.empty, 100000)
    rhs.peaks foreach { p => rhsBreeze(p._1) = p._2 }


    val realMass = query.pepmass.get * query.charge.get.charAt(0).toString.toInt
    val massFilter = (p: Peptide) => Math.abs(p.mass - realMass) < 20
    val dbPeptides = database.flatMap(_.splice()).filter(massFilter)
    println(s"count: ${dbPeptides.size}")
    println(s"dbPeptides ${dbPeptides.head.mass}")
    println(s"dbPeptides ${Math.abs(dbPeptides.head.mass - realMass)}")
    val res = dbPeptides.map { p =>
      val lhs = Spectrum.normalize(Spectrum.fromPeaks(p.yPeaks()))
      val lhsBreeze: SBV[Double] = new SBV[Double](Array.empty, Array.empty, 100000)
      lhs.peaks foreach { p => lhsBreeze(p._1) = p._2 }

      (breeze.linalg.functions.cosineDistance(lhsBreeze, rhsBreeze), p)
    }.sortWith((l, r) => l._1 < r._1).take(10)

    println(s"query: ${query}")
    println(s"found: $res")
  }

  def testQuery = {
    val query = queries("DGDKPEETQGK")
    val rhs = Spectrum.normalize(query.spectrum)
    val realMass = query.pepmass.get * query.charge.get.charAt(0).toString.toInt
    val massFilter = (p: Peptide) => Math.abs(p.mass - realMass) < 20
    val dbPeptides = database.flatMap(_.splice()).filter(massFilter)
    println(s"count: ${dbPeptides.size}")
    println(s"dbPeptides ${dbPeptides.head.mass}")
    println(s"dbPeptides ${Math.abs(dbPeptides.head.mass - realMass)}")
    val res = dbPeptides.map { p =>
      val lhs = Spectrum.normalize(Spectrum.fromPeaks(p.yPeaks()))
      (Spectrum.cosim(lhs, rhs), p)
    }.sortWith((l, r) => l._1 > r._1).head

    println(s"query: ${query}")
    println(s"found: $res")
  }

  def testFasta() = {
    println("TEST FASTA")
    val fastaPeptideSequence = database.head.splice()
    val generatedSpectrum1 = Spectrum.fromPeaks(fastaPeptideSequence(10).yPeaks())
    val generatedSpectrum2 = Spectrum.fromPeaks(fastaPeptideSequence(5).yPeaks())
    val n1 = generatedSpectrum1.normalize
    val n2 = generatedSpectrum2.normalize

    println(generatedSpectrum1.toString)
    println(generatedSpectrum2.toString)
    println(Spectrum.cosim(n1, n2))
    println("EOF TEST FASTA")
  }

  def testCosim() = {
    println(s"cosineSimilarity(SaS, PaP) = ${breezeImpl.Spectrum.cosim(SBV[Double](0.789, 0.515, 0.335, 0), SBV[Double](0.832, 0.555, 0, 0))}")
  }

  def testSpectrogramCosim() = {
    val queries = Spectrogram.fromMgf(mgfLines)

    val l = Spectrum.normalize(queries(0).spectrum)
    val map: Map[Double, Int] = (queries(0).peptide.get.yPeaks().reverse zip (0 to queries.head.peptide.get.acids.length).map(_ => 1)).toMap

    val sp: Spectrum = Spectrum(map)
    val r = Spectrum.normalize(sp)

    println(f"cosim(s0ref, s0) = ${Spectrum.cosim(l, r)}%1.4f")
  }

  def testPrintPeptide() = {
    println(queries.head._2.peptide.get.toString + "\n")
  }

  def testPrintAminoAcid() = {
    println(AminoAcid.get('K'))
  }
}