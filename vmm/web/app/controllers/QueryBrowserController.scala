package controllers

import javax.inject._

import akka.actor.ActorSystem
import peptidentifier._
import play.api.cache._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import upickle.default._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class QueryBrowserController @Inject()(cache: CacheApi, actorSystem: ActorSystem)(
  implicit exec: ExecutionContext,
  val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  def view(batchId: String) = Action.async { implicit request =>
    cache.get[Map[Int, Spectrogram]](batchId) match {
      case Some(queries) => Future.successful(Ok(views.html.query.viewQuery(batchId, queries)))
      case None => Future.successful(NotFound("ID not found."))
    }
  }

  def getAll() = Action.async { implicit request =>
    cache.get[List[String]]("_mgfIds") match {
      case Some(mgfIds) => Future.successful(Ok(write(mgfIds)))
      case None => Future.successful(Ok(write(List[String]())))
    }
  }

  def getMgf(batchId: String) = Action.async { implicit request =>
    val batch: Map[Int, Spectrogram] = cache.get[Map[Int, Spectrogram]](batchId).get

    val json = write(batch)
    Future.successful(Ok(json))
  }

  def run(batchId: String, queryId: Int, spectrumType: String, maxMassDiff: Int, proteinDbPath: String) = Action.async { implicit request =>
    val q = cache.get[Map[Int, Spectrogram]](batchId).get(queryId)
    val res: List[(Double, Peptide)] = spectrumType match {
      case "bSpectrum" => FastaDB.customPeaksQuery(q, maxMassDiff, (p: Peptide) => p.bPeaks()).take(10)
      case "ySpectrum" => FastaDB.customPeaksQuery(q, maxMassDiff, (p: Peptide) => p.yPeaks()).take(10)
      case "bySpectrum" => FastaDB.customPeaksQuery(q, maxMassDiff, (p: Peptide) => p.byPeaks()).take(10)
      case default => FastaDB.customPeaksQuery(q, maxMassDiff, (p: Peptide) => p.yPeaks()).take(10)
    }
    //val resOpt: List[(Double, Peptide)] = FastaDB.customPeaksQuery(q, maxMassDiff).take(10)

    Future.successful(Ok(write(res)))
  }
}
