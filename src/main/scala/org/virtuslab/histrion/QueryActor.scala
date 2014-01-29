package org.virtuslab.histrion

import akka.actor.Actor
import scala.slick.lifted.Query
import akka.pattern.pipe

/**
 * Created by Łukasz Dubiel.
 */
trait QueryActor extends Actor{
  implicit val executor : QueryExecutor

  protected implicit class QueryOperation[A,B](val query: Query[A, B]){
    def run() : Unit = executor.schedule(query).pipeTo(sender)
  }
}
