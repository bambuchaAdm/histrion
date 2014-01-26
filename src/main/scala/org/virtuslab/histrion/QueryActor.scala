package org.virtuslab.histrion

import akka.actor.Actor
import scala.slick.lifted.{Query, AbstractTable, TableQuery}

/**
 * Created by Łukasz Dubiel.
 */
trait QueryActor extends Actor{
  def executor : QueryExecutor

  implicit class QueryOperation(val query: Query[_,_]){
    def run : Unit = executor.schedule(query,sender)
  }
}
