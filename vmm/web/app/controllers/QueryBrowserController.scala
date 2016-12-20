package controllers

import javax.inject._

import akka.actor.ActorSystem
import forms.QueryForm
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import play.api.cache._
import peptidentifier._

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

@Singleton
class QueryBrowserController @Inject()(cache: CacheApi, actorSystem: ActorSystem)(
  implicit exec: ExecutionContext,
  val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  def view(query: String) = Action.async { implicit request =>
    cache.get[String](query) match {
      case Some(queriesRaw) =>
        Spectrogram.fromMgfIndexed(Source.fromString(queriesRaw).getLines) match {
          case queries if queries.isEmpty => Future.successful(NotFound("Invalid or empty input."))
          case queries => Future.successful(Ok(views.html.query.viewQuery(queries)))
        }
      case None => Future.successful(NotFound("ID not found."))
    }
  }
}
