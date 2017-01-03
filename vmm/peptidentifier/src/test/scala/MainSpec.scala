import breeze.linalg.{SparseVector => SBV}
import peptidentifier._

import org.scalatest._

class MainSpec extends FlatSpec {

  val mgfLines = scala.io.Source.fromFile("/var/my_root/repos/fit/vmm/input/spectra/amethyst_annotated.mgf").getLines
  val queries = Spectrogram.fromMgfIndexed(mgfLines)

  /*var ok, err = 0
  queries foreach { q =>
    if (FastaDB.query(q._2, 10).head._2.toString == q._2.peptide.get.toString) ok += 1
    else err += 1
    println(s"$ok ok, $err err")
  }*/

  def testQuery1 = {
    val query = queries("DGDKPEETQGK")
    val rhs = Spectrum.normalize(query.spectrum)
    //val lhsPeptide = Peptide.fromString("EVCASCHSLSR")
    val lhsPeptide = Peptide.fromString("DGDKPEETQGK")
    val lhs = Spectrum.normalize(Spectrum.fromPeaks(lhsPeptide.yPeaks()))

    val cosim = breeze.linalg.functions.cosineDistance(lhs.peaks, rhs.peaks)

    val realQueryMass = query.pepmass.get * query.charge.get.charAt(0).toString.toInt
    val massDiff = Math.abs(realQueryMass - lhsPeptide.mass)

    println(lhs)
    println(rhs)
    println(s"cosim: $cosim, massdiff: $massDiff")
  }

  def testPrintPeptide() = {
    println(queries.head._2.peptide.get.toString + "\n")
  }

  def testPrintAminoAcid() = {
    println(AminoAcid.get('K'))
  }

  "Searching the database" should "yield a result" in {
    val resOpt = FastaDB.query(queries("DGDKPEETQGK"), 0.2).headOption
    resOpt match {
      case Some(res) =>
        info(res.toString)
        assert(true)
      case None => assert(false)
    }
  }
}