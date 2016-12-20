package controllers

import javax.inject._

import akka.actor.ActorSystem
import forms.QueryForm
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.cache._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

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
        val id = java.util.UUID.randomUUID().toString
        cache.set(id, data.queries)
        Future.successful(Redirect(routes.QueryBrowserController.view(id)).flashing("info" -> Messages(s"Your query ID is $id")))
      }
    )
  }

}
