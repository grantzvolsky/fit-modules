package client.pages

import org.scalajs.dom.Element
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Div
import org.scalajs.{dom => sjsdom}
import upickle.default.read

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scalatags.JsDom.all._


object MgfListPage {
  val root: Div = div(id:="sjsContainer")(page()).render

  case class MenuItem(id: String) {
    def render = {
      div(
        a(href:=js.Dynamic.global.jsRoutes.controllers.QueryBrowserController.view(id).absoluteURL().toString())(id),
        br()
      ).render
    }
  }

  def page() = {
    val allMgfUrl = js.Dynamic.global.jsRoutes.controllers.QueryBrowserController.getAll().absoluteURL().toString

    Ajax.get(allMgfUrl).onSuccess { case xhr =>
      val mgfIds: List[String] = read[List[String]](xhr.responseText)
      mgfIds foreach { id =>
        root.appendChild(MenuItem(id).render)
      }
    }
  }

  def render(container: Element) = {
    container.parentNode.replaceChild(root, container)
  }
}