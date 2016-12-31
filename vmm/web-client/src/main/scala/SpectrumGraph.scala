package client


import org.scalajs.{dom => sjsdom}

import scala.scalajs.js.Dynamic.{global => sg}
import shared.Spectrogram

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSName}

object SpectrumGraph {
  @JSExport
  def render(container: sjsdom.html.Div, s: Spectrogram): Unit = {
    //val packages = js.Dynamic.literal(`package` = Seq("corechart").toJSArray)
    //sg.google.charts.load("current",  packages)

    // Define the chart to be drawn.
    //val data = new DataTable() //js.Dynamic.newInstance(js.Dynamic.global.google.visualization.DataTable)()
    //data.addColumn("number", "m/z")
    //data.addColumn("number", "intensity 1")
    //data.addColumn("number", "intensity 2")


    val rows: Seq[Seq[Any]] = Seq(Seq("m/z", "intensity1", "intensity2")) ++
      s.spectrum.peaks.toSeq.map(p => Seq(p._1, p._2, null)) ++
      s.spectrum.peaks.toSeq.map(p => Seq(p._1, null, p._2 * 2))

    val data = js.Dynamic.global.google.visualization.arrayToDataTable(rows.map(r => r.toJSArray).toJSArray)

    // Instantiate and draw the chart.
    val chart = new ScatterChart(sjsdom.document.getElementById("gchart").asInstanceOf[sjsdom.html.Div])
    val options = js.Dynamic.literal(
      chart = js.Dynamic.literal(
        title = s"${s.title} (${s.peptide})"
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

/*
import org.scalajs.{dom => sjsdom}
import shared.Spectrogram

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSName}
import scala.scalajs.js.JSConverters._
import upickle.default.write


case class Pt(x: Double, y: Double)

object SpectrumGraph {
  @JSExport
  def render(canvas: sjsdom.html.Canvas, s: Spectrogram): Unit = {
    val ctx = canvas.getContext("2d")
    //val t = new JSChart(ctx).Line(ChartData(Seq("A"), Seq(ChartDataset(Seq(Pt(1, 5), Pt(2, -1), Pt(3, 3)), "Data1"))))
    //t.build
  }

  trait ChartDataset extends js.Object {
    def label: String = js.native
    def fillColor: String = js.native
    def strokeColor: String = js.native
    def data: js.Array[Double] = js.native
  }

  object ChartDataset {
    def apply(data: Seq[Pt], label: String, fillColor: String = "#8080FF", strokeColor: String = "#404080"): ChartDataset = {
      js.Dynamic.literal(
        data = data.map(p => js.Dynamic.literal(x = p.x, y = p.y)).toJSArray,
        label = label,
        fillColor = fillColor,
        strokeColor = strokeColor
      ).asInstanceOf[ChartDataset]
    }
  }

  trait ChartData extends js.Object {
    def labels: js.Array[String] = js.native
    def datasets: js.Array[ChartDataset] = js.native
  }

  object ChartData {
    def apply(labels: Seq[String], datasets: Seq[ChartDataset]): ChartData = {
      js.Dynamic.literal(
        //labels = labels.toJSArray,
        data = js.Dynamic.literal(datasets = datasets.toJSArray)
      ).asInstanceOf[ChartData]
    }
  }

  // define a class to access the Chart.js component
  @JSName("Chart")
  class JSChart(ctx: js.Dynamic) extends js.Object {
    // create different kinds of charts
    def Line(data: ChartData): js.Dynamic = js.native
    def Bar(data: ChartData): js.Dynamic = js.native
  }
}
*/