package models

import akka.actor.{ActorLogging, Actor, Props}
import play.api.libs.json.JsValue

class Onderdeel extends Actor with ActorLogging {

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
    self ! ReceivedData(value)
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
