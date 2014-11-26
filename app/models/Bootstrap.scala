package models

import java.time.LocalDateTime

import akka.actor.{Props, Actor}

/**
 * Bootstrap Actor. Will initial the AbbaMeldaProcessor
 */
class Bootstrap(onderdeelFactory:OnderdeelFactory) extends Actor {

  import Bootstrap._


  def fetchCursor = {
    // TODO: last cursor position must come from storage
    Cursor(0, LocalDateTime.of(1970, 1, 1, 0, 0, 0))
  }

  def start(): Unit = {
    // TODO: recover from eventual fetchCursor failure
    // AbbaMeldaProcessor needs the last cursor position ot start
    // system can only be active when the cursor is fetched
    val cursor = fetchCursor

    val abbaMeldaProc = context.system.actorOf(
      AbbaMeldaProcessor.props(cursor, onderdeelFactory),
      AbbaMeldaProcessor.name
    )

    // start synchronization
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