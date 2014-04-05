package org.virtuslab.histrion

import akka.actor.{ActorRef, Actor}

/**
 * Created by bambucha on 05.04.14.
 */
class UsageActor(repository: ActorRef) extends Actor{

  import MappingQueryActorProtocol._

  def receive = {
    case _ =>
      repository ! GetAll
      context.become(queryResult)
  }

  def queryResult : Actor.Receive = {
    case result : Iterable[Person] => result.foreach( println(_))
  }
}
