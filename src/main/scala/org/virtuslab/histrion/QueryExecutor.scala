package org.virtuslab.histrion

import scala.slick.lifted.Query
import akka.actor.ActorRef

/**
 * Created by Łukasz Dubiel.
 */
class QueryExecutor {
  def schedule(query: Query[_,_], recipient: ActorRef) : Unit = ???
}
