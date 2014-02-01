package org.virtuslab.histrion

import akka.actor.Actor
import scala.slick.driver.H2Driver.simple._

object TestQueryActorProtocol {
  object GetAll

  case class ById(id: Int)

  case class ByValue(value: Int)

  case class Delete(id: Int)

  case class Update(id: Int, value: Int)
}

class TestQueryActor(val table: TableQuery[TestTable], val executor: QueryExecutor) extends QueryActor {

  import TestQueryActorProtocol._

  def byId(id: Column[Int]) = table.filter(_.id === id)

  def byValue(value: Column[Int]) = table.filter(_.value === value)

  def updateValueById(id: Column[Int]) = table.filter(_.id === id).map(_.value)

  val query = table

  def receive: Actor.Receive = {
    case GetAll => query.run()
    case ById(id) => byId(id).run
    case ByValue(value) => byValue(value).run
    case Delete(id) => byId(id).deleteAll
    case Update(id, value) => updateValueById(id).updateWith(value)
  }
}
