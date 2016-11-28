package peptidentifier

import breeze.linalg.{SparseVector => SBV}


object Main extends App {
  println(AminoAcid.get('K'))

  val mgfLines = scala.io.Source.fromFile("/var/my_root/repos/fit/vmm/input/spectra/amethyst_annotated.mgf").getLines

  val queries = Spectrogram.fromMgf(mgfLines)

  println(s"cosineSimilarity(SaS, PaP) = ${breezeImpl.Spectrum.cosim(SBV[Double](0.789, 0.515, 0.335, 0), SBV[Double](0.832, 0.555, 0, 0))}")

  /*println(s"s0ref\t= $s0ref")
  println(s"s0\t= $s0")
  println(s"s1ref\t= $s1ref")
  println(s"s1\t= $s1")
  println(s"s2ref\t= $s2ref")
  println(s"s2\t= $s2")*/



  val l = Spectrum.normalize(queries(0).spectrum)
  val map: Map[Double, Int] = (queries(0).peptide.get.ySpectrum().reverse zip (0 to queries.head.peptide.get.acids.length).map(_ => 1)).toMap
  val sp: Spectrum = Spectrum(map)
  val r = Spectrum.normalize(sp)

  println(f"cosim(s0ref, s0) = ${Spectrum.cosim(l, r)}%1.4f")
  println(f"cosim(s0ref, s0) = ${Spectrum.cosim(l, r)}%1.4f")

  /*println(f"cosim(s0, s0ref) = ${cosim(s0, s0ref)}%1.4f, ${mergeSpectra(sgrams(0).spectrum.peaks.reverse, sgrams(0).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s0, s2ref) = ${cosim(s0, s2ref)}%1.4f, ${mergeSpectra(sgrams(0).spectrum.peaks.reverse, sgrams(2).peptide.get.ySpectrum(), 1.01)}")*/

  /*val builder = new CSCMatrix.Builder[Double](rows = -1, cols = 2)
  builder.add(sv1.asCscColumn)

  sv1.asCscColumn
  val newm =
  val rows: RDD[Vector] = sc.parallelize(sv1)
  val mat = new RowMatrix(rows)
  println(sv1)*/

  //println(sgrams.head.peptide.get.toString + "\n")
  /*println(sgrams.head.peptide.get.bSpectrum() + "\n")
  println(sgrams.head.peaks.reverse + "\n")

  println(mergeSpectra(sgrams.head.peaks.reverse, sgrams.head.peaks.reverse.map(p => p._1), 0.5))

  println(mergeSpectra(sgrams.head.peaks.reverse, sgrams.head.peptide.get.ySpectrum(), 0.5))
  println(mergeSpectra(sgrams.head.peaks.reverse, sgrams.head.peptide.get.bSpectrum(), 0.5))*/

  /*val referenceSpectrumVector = SparseVector.zeros[Int](40000)
  sgrams.head.peptide.get.acids

  sgrams.head.peptide match {
    case Some(peptide) =>
      println(peptide.toString)
      sgrams.head.peaks foreach {peak: (Double, Int) => sv((peak._1*10).toInt) = peak._2}
    case None => println("Spectrum has no reference peptide.")
  }*/

}