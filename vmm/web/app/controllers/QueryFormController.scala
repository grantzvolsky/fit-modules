package controllers

import javax.inject._

import akka.actor.ActorSystem
import forms.QueryForm
import peptidentifier.Spectrogram
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.cache._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

@Singleton
class QueryFormController @Inject()(cache: CacheApi, actorSystem: ActorSystem)(
  implicit exec: ExecutionContext,
  val messagesApi: MessagesApi
) extends Controller with I18nSupport {

  def view = Action.async { implicit request =>
    Future.successful(Ok(views.html.query.form.view(QueryForm.form)))
  }

  def submit = Action.async { implicit request =>
    QueryForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.query.form.view(form))),
      data => {
        Spectrogram.fromMgf(Source.fromString(data.queries).getLines).zipWithIndex.map(_.swap).toMap match {
          case queries if queries.isEmpty => Future.successful(NotFound("Invalid or empty input."))
          case queries =>
            val id = java.util.UUID.randomUUID().toString
            cache.set(id, queries)
            Future.successful(Redirect(routes.QueryBrowserController.view(id)).flashing("info" -> Messages(s"Your query ID is $id")))
        }
      }
    )
  }

}
