package client

import client.pages.{MgfDetailPage, MgfListPage}
import org.scalajs.dom.raw.Element
import org.scalajs.{dom => sjsdom}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => sjsglobal}
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@js.native
trait HTMLElementExt extends Element {
  def dataset: js.Dictionary[String] = js.native
}

object HTMLElementExt {
  implicit def elem2extElem(elem: Element): HTMLElementExt = elem.asInstanceOf[HTMLElementExt]
}

object Modules extends JSApp {
  def main() = {
    println("Hello World")
  }

  @JSExport
  def mgfListPage() = {
    val container = sjsdom.document.getElementById("sjsContainer")
    MgfListPage.render(container)
  }

  @JSExport
  def mgfDetailPage() = MgfDetailPage.mgfDetailPage()
}