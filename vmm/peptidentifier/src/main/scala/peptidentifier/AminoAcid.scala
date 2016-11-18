package peptidentifier

import scala.collection.immutable

case class AminoAcid(name: String,
                     code: Char,
                     mono: Double, // monoisotopic mass
                     avg: Double /* isotopic average mass*/ )

case object AminoAcid {
  def fromString(str: String): Option[AminoAcid] = {
    val inRegex = raw"(.*) (.*) (.*) (.*)".r
    str match {
      case inRegex(name, c, m, a) => Some(AminoAcid(name, c.charAt(0), m.toDouble, a.toDouble))
      case default => None
    }
  }

  val in =
    """|Gly G 57.021464 57.05
      |Ala A 71.037114 71.08
      |Ser S 87.032029 87.08
      |Pro P 97.052764 97.12
      |Val V 99.068414 99.07
      |Thr T 101.04768 101.1
      |Cys C 103.00919 103.1
      |Leu L 113.08406 113.2
      |Ile I 113.08406 113.2
      |Asn N 114.04293 114.1
      |Asp D 115.02694 115.1
      |Gln Q 128.05858 128.1
      |Lys K 128.09496 128.2
      |Glu E 129.04259 129.1
      |Met M 131.04048 131.2
      |His H 137.05891 137.1
      |Phe F 147.06841 147.2
      |Arg R 156.10111 156.2
      |Tyr Y 163.06333 163.2
      |Trp W 186.07931 186.2""".stripMargin

  val inRegex = raw"(.*) (.*) (.*) (.*)".r

  val acids: immutable.Map[Char, AminoAcid] = {for {
    line <- in.split('\n')
    acid = fromString(line).get
  } yield (acid.code, acid)}.toMap

  def get(code: Char) = acids.get(code)
}