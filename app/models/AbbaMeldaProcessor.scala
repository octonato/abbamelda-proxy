package models

import akka.actor.{ActorLogging, Actor, Props}
import akka.pattern.pipe
import models.Onderdeel.StatusChanged
import play.api.libs.json.{JsArray, JsValue}
import services.AbbaMeldaService

class AbbaMeldaProcessor(cursor: Cursor, onderdeelFactory: OnderdeelFactory) extends Actor with ActorLogging {

  import context.dispatcher
  import models.AbbaMeldaProcessor._


  override def receive = {
    case FetchOnderdelenStatus => fetchStatus()
    case ReceivedStatus(statuses) => dispatch(statuses)
  }


  def fetchStatus(): Unit = {


    val futureRes = AbbaMeldaService.fetchStatuses(cursor.lastPosition, cursor.lastUpdate)

    val receivedStatus = {
      // Futures from WS uses Play ExecutionContext
      import concurrent.Execution.wsExecutionContext
      futureRes.map { res =>
        val onderdelen = (res \ "onderdelen").as[JsArray]
        ReceivedStatus(onderdelen.value)
      }
    }

    // fetch is async, when it completes we send results to self
    receivedStatus pipeTo self

  }

  def dispatch(onderdelenStatus: Seq[JsValue]): Unit = {

    // each entry in status list points to one 'onderdeel'
    onderdelenStatus.foreach { onderdeelJs =>

      log.info(s"onderdel json: $onderdeelJs")

      val id = (onderdeelJs \ "id").as[Long]
      // find corresponding Onderdeel Actor (for the moment we have only one)
      val onderdeel = onderdeelFactory.findById(id)

      // notify it to update himself
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