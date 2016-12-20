package forms

import play.api.data.Form
import play.api.data.Forms._

object QueryForm {
  val form = Form(
    mapping(
      "queries" -> text
    )(Data.apply)(Data.unapply)
  )

  case class Data(queries: String)
}