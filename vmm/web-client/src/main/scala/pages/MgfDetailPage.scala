package client.pages

import client._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.{Div, TableRow}
import org.scalajs.{dom => sjsdom}
import shared.{Peptide, Spectrogram, Spectrum}
import upickle.default.read

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => sjsglobal}
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

object MgfDetailPage {
  val menuElem: HTMLElementExt = sjsdom.document.getElementById("spectraMenu").asInstanceOf[HTMLElementExt]
  val mgfId: String = menuElem.dataset.get("batchid").get
  val mgfUrl: String = sjsglobal.jsRoutes.controllers.QueryBrowserController.getMgf(mgfId).absoluteURL().toString

  case class MatchingPeptide(query: Spectrogram, similarity: Double, peptide: Peptide) {
    def dom: TypedTag[TableRow] = {
      val virtualSpectrum = js.Dynamic.global.document.querySelector("input[name=\"spectrumType\"]:checked").value.toString match {
        case "bSpectrum" => Spectrum.fromPeaks(peptide.bPeaks())
        case "ySpectrum" => Spectrum.fromPeaks(peptide.yPeaks())
        case "bySpectrum" => Spectrum.fromPeaks(peptide.byPeaks())
        case default => Spectrum.fromPeaks(peptide.yPeaks())
      }
      tr(
        td(peptide.toString),
        td(similarity),
        td(a(href:="javascript:void(0);", onclick:={()=>println(SpectrumGraph.render(sjsdom.document.getElementById("gchart").asInstanceOf[Div], query, virtualSpectrum))})("compare to query"))
      )
    }
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
  def mgfDetailPage() = {
    Ajax.get(mgfUrl).onSuccess { case xhr =>
      val menu = read[Map[Int, Spectrogram]](xhr.responseText)
      menuElem.appendChild(client.pages.MgfDetailPage.populateSpectraMenu(menu).render)
    }
  }

  @JSExport
  def detail(spectrumId: Int, s: Spectrogram) = {

    val detailElem = sjsdom.document.getElementById("spectraDetail").asInstanceOf[HTMLElementExt]

    val d = div(
      div(id:="detailSummary")(
        h2("Spectrum Detail"),
        table(
          tr(td("label"), td(s.peptide.toString)),
          tr(td("mass"), td(s.pepmass)),
          tr(td("charge"),td(s.charge)),
          tr(td("title"), td(s.title))
        ),
        h2("Find spectrum"),
        div(
          input(`type`:="radio", name:="spectrumType", value:="ySpectrum", checked:="checked"),"ySpectrum",
          input(`type`:="radio", name:="spectrumType", value:="bSpectrum"),"bSpectrum",
          input(`type`:="radio", name:="spectrumType", value:="bySpectrum"),"bySpectrum",
          br(),"maximum mass difference ",input(`type`:="text", name:="maxMassDiff", value:=10),
          br(),"path to protein database (fasta)",input(`type`:="text", name:="proteinDatabasePath", value:="/var/my_root/repos/fit/vmm/input/sequence_database/amop_msdb_10000.fasta")
        ),
        p(a(href:="javascript:void(0);", onclick:={()=>identify(spectrumId, s)})("find"))
      ),
      div(id:="detailMatches")("no results yet")
    ).render

    if (detailElem.childNodes.length > 0) detailElem.replaceChild(d, detailElem.lastChild)
    else detailElem.appendChild(d)
  }

  @JSExport
  def identify(queryId: Int, s: Spectrogram): Unit = {
    val spectrumType: String = js.Dynamic.global.document.querySelector("input[name=\"spectrumType\"]:checked").value.toString
    val maxMassDiff: Int = js.Dynamic.global.document.querySelector("input[name=\"maxMassDiff\"]").value.toString.toInt
    val proteinDbPath: String = js.Dynamic.global.document.querySelector("input[name=\"proteinDatabasePath\"]").value.toString

    val identifyActionUrl = sjsglobal.jsRoutes.controllers.QueryBrowserController.run(mgfId, queryId, spectrumType, maxMassDiff, proteinDbPath).absoluteURL().toString

    Ajax.get(identifyActionUrl).onSuccess { case xhr =>
      val res: List[(Double, Peptide)] = read[List[(Double, Peptide)]](xhr.responseText)
      val matchesContainer = sjsdom.document.getElementById("detailMatches")
      val newChild = div(id:="detailMatches")(
        res.map(m => MatchingPeptide(s, m._1, m._2).dom)
      ).render

      matchesContainer.parentNode.replaceChild(newChild, matchesContainer)
    }
  }
}