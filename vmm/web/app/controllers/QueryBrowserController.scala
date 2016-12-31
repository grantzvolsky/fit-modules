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

  def getJson(batchId: String) = Action.async { implicit request =>
    val batch: Map[Int, Spectrogram] = cache.get[Map[Int, Spectrogram]](batchId).get

    /*implicit object AminoAcidWrites extends Writes[List[AminoAcid]] {
      def writes(o: List[AminoAcid]) = JsString(o.map(_.code).mkString(""))
    }
    implicit val PeptideWrites = Json.writes[Peptide]

    implicit def mapWrites[A, B](implicit a: Writes[A], b: Writes[B]): Writes[Map[A, B]] = new Writes[Map[A, B]] {
      def writes(o: Map[A, B]): JsValue = JsArray(o.map{ case (k, v) => Json.obj("_1" -> k.toString, "_2" -> b.writes(v)) }.toSeq)   //a.writes(tuple._1), b.writes(tuple._2)))
    }*/

    //implicit val SpectrumWrites = Json.writes[Spectrum]

    //implicit val SpectrogramWrites = Json.writes[Spectrogram]



    val json = write(batch)
    Future.successful(Ok(json))
  }

  def run(batchId: String, queryId: Int) = Action.async { implicit request =>
    val q = cache.get[Map[Int, Spectrogram]](batchId).get(queryId)
    val resOpt = FastaDB.query(q, 10).take(10)

    /*implicit object PeptideiteWrites extends Writes[Peptide] {
      def writes(o: Peptide) = JsString(o.toString)
    }

    implicit def tuple2Writes[A, B](implicit a: Writes[A], b: Writes[B]): Writes[Tuple2[A, B]] = new Writes[Tuple2[A, B]] {
      def writes(tuple: Tuple2[A, B]) = JsArray(Seq(a.writes(tuple._1), b.writes(tuple._2)))
    }*/

    val json = write(resOpt)
    Future.successful(Ok(json))
  }
}
