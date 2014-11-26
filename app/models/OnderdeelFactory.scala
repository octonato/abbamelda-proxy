package models

import akka.actor.{ActorRef, ActorSystem}

case class OnderdeelFactory(actorSystem:ActorSystem) {

  private var single: Option[ActorRef] = None

  def findById(id:Long) : ActorRef = {

    val actor = single.getOrElse {
      actorSystem.actorOf(Onderdeel.props, "onderdeel")
    }

    single = Option(actor)

    actor
  }
}
