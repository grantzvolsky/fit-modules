package shared

case class Spectrum(peaks: Map[Double, Int])

case object Spectrum {
  def fromPeaks(peaks: List[Double]) = Spectrum(peaks zip Stream.continually(1) toMap)
}