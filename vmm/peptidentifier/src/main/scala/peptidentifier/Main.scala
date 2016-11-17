package peptidentifier

object Main extends App {

  println(AminoAcid.get('K'))

  val mgfLines = io.Source.fromFile("/var/my_root/repos/MI-VMM/input/spectra/amethyst_annotated.mgf").getLines

  val sgrams = Spectrogram.fromMgf(mgfLines)
  val peptideMassSum = sgrams.head.peptide.map(_.mono).sum
  println("peptideMassSum: " + peptideMassSum)

  //println(sgrams.head)
}