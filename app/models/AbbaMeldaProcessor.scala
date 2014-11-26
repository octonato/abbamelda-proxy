package models

import akka.actor.{Stash, FSM, Props, Actor}
import models.Onderdeel.StatusChanged
import play.api.Play
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSResponse, WS}
import akka.pattern.pipe
import services.AbbaMeldaService

import scala.concurrent.Future

class AbbaMeldaProcessor(cursor: Cursor, onderdeelFactory: OnderdeelFactory) extends Actor {

  import models.AbbaMeldaProcessor._



  override def receive: Receive = {
    case FetchOnderdelenStatus => fetchStatus()
    case ReceivedStatus(statuses) => dispatch(statuses)
  }


  def fetchStatus(): Unit = {

    val futureRes = AbbaMeldaService.fetchStatuses(cursor.lastPosition, cursor.lastUpdate)

    val receivedStatus = futureRes.map { res =>
      ReceivedStatus(res \\ "onderdelen")
    }

    receivedStatus pipeTo self

  }

  def dispatch(onderdelenStatus: Seq[JsValue]): Unit = {
    onderdelenStatus.foreach { onderdeelJs =>

      val id = (onderdeelJs \ "id").as[Long]
      val onderdeel = onderdeelFactory.findById(id)

      onderdeel ! StatusChanged(onderdeelJs)

    }
  }


}

object AbbaMeldaProcessor {


  // Actor
  val name: String = "AbbaMeldaProcessor"
  def props(cursor: Cursor, onderdeelFactory: OnderdeelFactory): Props = Props(new AbbaMeldaProcessor(cursor, onderdeelFactory))


  // Messages
  case object FetchOnderdelenStatus

  case class ReceivedStatus(onderdelen: Seq[JsValue])

}