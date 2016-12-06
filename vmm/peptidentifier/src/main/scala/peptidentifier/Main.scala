package peptidentifier

import breeze.linalg.{SparseVector => SBV}


object Main extends App {

  val mgfLines = scala.io.Source.fromFile("/var/my_root/repos/fit/vmm/input/spectra/amethyst_annotated.mgf").getLines
  val queries = Spectrogram.fromMgf(mgfLines)

  val fastaLines = scala.io.Source.fromFile("/var/my_root/repos/fit/vmm/input/sequence_database/amop_msdb_10000.fasta").getLines
  val database = PeptideSequence.fromFasta(fastaLines)

  testFasta()

  def testFasta() = {
    println("TEST FASTA")
    val fastaPeptideSequence = database.head.splice()
    val generatedSpectrum1 = Spectrum.fromPeaks(Peptide.fromString(fastaPeptideSequence(10)).yPeaks())
    val generatedSpectrum2 = Spectrum.fromPeaks(Peptide.fromString(fastaPeptideSequence(5)).yPeaks())
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
    val l = Spectrum.normalize(queries(0).spectrum)
    val map: Map[Double, Int] = (queries(0).peptide.get.yPeaks().reverse zip (0 to queries.head.peptide.get.acids.length).map(_ => 1)).toMap
    val sp: Spectrum = Spectrum(map)
    val r = Spectrum.normalize(sp)

    println(f"cosim(s0ref, s0) = ${Spectrum.cosim(l, r)}%1.4f")
  }

  def testPrintPeptide() = {
    println(queries.head.peptide.get.toString + "\n")
  }

  def testPrintAminoAcid() = {
    println(AminoAcid.get('K'))
  }
}