package client

import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.Element
import org.scalajs.{dom => sjsdom}
import shared.Spectrogram
import upickle.default.read

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => sjsglobal}
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom._
import scalatags.JsDom.all._

object Modules extends JSApp {
  def main() = {
    println("Hello World")
  }

  @js.native
  trait HTMLElementExt extends Element {
    def dataset: js.Dictionary[String] = js.native
  }

  object HTMLElementExt {
    implicit def elem2extElem(elem: Element): HTMLElementExt = elem.asInstanceOf[HTMLElementExt]
  }

  def populateSpectraMenu(menu: Map[Int, Spectrogram]): TypedTag[sjsdom.raw.HTMLElement] = {
    div(
      h2(s"spectra"),
      p(s"spectra ${menu.size}"),
      table(
        tr(th("spectrum_id"), th("label"), th("actions")),
        for ((id, s) <- menu.toSeq) yield tr(td(id), td(s.peptide.toString), a("detail", href:="javascript:void(0);", onclick:={()=>detail(id, s)}))
      )
    )
  }

  @JSExport
  def load() = {
    val menuElem = sjsdom.document.getElementById("spectraMenu").asInstanceOf[HTMLElementExt]
    val batchIdOpt: Option[String] = menuElem.dataset.get("batchid")
    val queryBatchUrlOpt = batchIdOpt map (batchId => sjsglobal.jsRoutes.controllers.QueryBrowserController.getJson(batchId).absoluteURL().toString)

    Ajax.get(queryBatchUrlOpt.get).onSuccess { case xhr =>
      val menu = read[Map[Int, Spectrogram]](xhr.responseText)
      menuElem.appendChild(populateSpectraMenu(menu).render)
    }
  }

  @JSExport
  def detail(id: Int, s: Spectrogram) = {
    SpectrumGraph.render(sjsdom.document.getElementById("gchart").asInstanceOf[Div], s)

    val detailElem = sjsdom.document.getElementById("spectraDetail").asInstanceOf[HTMLElementExt]

    val d = div(
      h2("Detail"),
      table(
        tr(td("label"), td(s.peptide.toString)),
        tr(td("mass"), td(s.pepmass)),
        tr(td("charge"),td(s.charge)),
        tr(td("title"), td(s.title))
      )
    ).render

    if (detailElem.childNodes.length > 0) detailElem.replaceChild(d, detailElem.lastChild)
    else detailElem.appendChild(d)
  }

  @JSExport
  def identify(id: Int) = {
    val batchIdOpt: Option[String] = sjsdom.document.getElementById("queryBatch").asInstanceOf[HTMLElementExt].dataset.get("batchid")
    val identifyActionUrlOpt: Option[String] = batchIdOpt map (batchId => sjsglobal.jsRoutes.controllers.QueryBrowserController.run(batchId, id).absoluteURL().toString)

    Ajax.get(identifyActionUrlOpt.get).onSuccess { case xhr =>
        println(xhr.responseText)
    }
  }



}