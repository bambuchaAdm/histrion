package org.virtuslab.histrion

import akka.actor.Actor
import scala.slick.lifted.Query
import akka.pattern.pipe
import scala.slick.driver.H2Driver.simple._

/**
 * Created by Łukasz Dubiel.
 */
trait QueryActor extends Actor{
  implicit val executor : QueryExecutor

  protected implicit class QueryOperation[A <: Table[_], B](val query: Query[A, B]){
    def run() : Unit = executor.scheduleSelect(query).pipeTo(sender)
    def deleteAll() : Unit = executor.scheduleDelete(query).pipeTo(sender)
  }

  protected implicit class UpdateOperation[A <: Column[_], B](val query: Query[A,B]){
    def updateWith(value: B) = executor.scheduleUpdate(query,value).pipeTo(sender)
  }
}
