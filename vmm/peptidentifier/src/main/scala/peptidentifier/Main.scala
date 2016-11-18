package peptidentifier

object Main extends App {
  println(AminoAcid.get('K'))

  val mgfLines = io.Source.fromFile("/var/my_root/repos/fit/vmm/input/spectra/amethyst_annotated.mgf").getLines

  val sgrams = Spectrogram.fromMgf(mgfLines)
  sgrams.head.peptide match {
    case Some(peptide) =>
      println(peptide.toString)
      sgrams.head.peaks foreach println
    case None => println("Spectrum has no reference peptide.")
  }

}