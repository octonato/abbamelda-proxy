package models

import java.time.LocalDateTime

import akka.actor.{Props, Actor}

class Bootstrap(onderdeelFactory:OnderdeelFactory) extends Actor {

  import Bootstrap._

  val fetchCursor = Cursor(0, new LocalDateTime())

  def start(): Unit = {
    // TODO: recover from eventual fetchCursor failure
    val cursor = fetchCursor

    val abbaMeldaProc = context.system.actorOf(
      AbbaMeldaProcessor.props(cursor, onderdeelFactory),
      AbbaMeldaProcessor.name
    )

    abbaMeldaProc ! AbbaMeldaProcessor.FetchOnderdelenStatus
  }

  override def receive: Receive = {
    case Start => start()
  }

}

object Bootstrap {

  case object Start

  val name = "BootstrapActor"
  def props(onderdeelFactory:OnderdeelFactory) : Props = Props(new Bootstrap(onderdeelFactory))
}