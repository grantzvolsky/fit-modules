package client


import org.scalajs.{dom => sjsdom}

import scala.scalajs.js.Dynamic.{global => sg}
import shared.{Spectrogram, Spectrum}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSName}

object SpectrumGraph {
  @JSExport
  def render(container: sjsdom.html.Div, query: Spectrogram, virtualSpectrum: Spectrum): Unit = {
    val rows: Seq[Seq[Any]] = Seq(Seq("m/z", "intensity1", "intensity2")) ++
      query.spectrum.peaks.toSeq.map(p => Seq(p._1, p._2, null)) ++
      virtualSpectrum.peaks.toSeq.map(p => Seq(p._1, null, p._2 * 2))

    val data = js.Dynamic.global.google.visualization.arrayToDataTable(rows.map(r => r.toJSArray).toJSArray)

    // Instantiate and draw the chart.
    val chart = new ScatterChart(sjsdom.document.getElementById("gchart").asInstanceOf[sjsdom.html.Div])
    val options = js.Dynamic.literal(
      chart = js.Dynamic.literal(
        title = s"${query.title} (${query.peptide})"
      ),
      pointShape = "circle",
      pointSize = "2"
    )

    chart.draw(data, options)

  }

  @JSName("google.visualization.DataTable")
  @js.native
  class DataTable extends js.Object {
    def addColumn(`type`: String, value: String): js.Dynamic = js.native
    def addRows(param: js.Array[scala.scalajs.js.Array[Any]]): js.Dynamic = js.native
  }

  @JSName("google.visualization.ScatterChart")
  class ScatterChart(elem: sjsdom.html.Div) extends js.Object {
    def draw(data: js.Dynamic, opts: Any): js.Dynamic = js.native
  }
}