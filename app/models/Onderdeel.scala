package models

import akka.actor.{ActorLogging, Actor, Props}
import play.api.libs.json.JsValue
import akka.pattern.pipe
import services.AbbaMeldaService

import scala.concurrent.Future

class Onderdeel extends Actor with ActorLogging {

  import context.dispatcher
  import models.Onderdeel._

  private var data: Option[JsValue] = None


  override def receive = {

    case StatusChanged(jsValue) =>
      log.info(s"status changed: $jsValue")
      fetchData(jsValue)

    case ReceivedData(jsValue) =>
      log.info(s"received data: $jsValue")
      data = Option(jsValue)

    case SendData =>
      log.info(s"data requested, sending: $data")
      sender() ! data

  }

  def fetchData(value: JsValue): Unit = {

    val id = (value \ "id").as[Long]
    val futureRes = AbbaMeldaService.fetchOnderdeel(id)

    val receivedData = {
      import concurrent.Execution.wsExecutionContext
      futureRes.map { res =>
        ReceivedData(res)
      }
    }

    receivedData pipeTo self
  }
}

object Onderdeel {
  // Actor
  def props : Props = Props(new Onderdeel)

  // Messages
  case class StatusChanged(jsValue:JsValue)
  case class ReceivedData(jsValue:JsValue)
  case object SendData

}
