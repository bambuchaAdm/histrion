package org.virtuslab.histrion

import akka.actor.Actor
import scala.slick.lifted.TableQuery


object MappingQueryActorProtocol {
  object GetAll
  case class ById(id: Long)
  case class ByNick(nick: String)
  case class ByFirstNameAndSureName(first: String, second: String)
}

class MappingQueryActor(table: TableQuery[MappingTable], val executor: QueryExecutor) extends  QueryActor {
  import MappingQueryActorProtocol._

  import slick.driver.H2Driver.simple._

  def findByFirstNameAndSureName(first: String, sure: String) = {
    table.filter(row => row.firstName === first && row.sureName === sure)
  }

  def findById(id: Long) = {
    table.filter(_.id === id)
  }

  def receive: Actor.Receive = {
    case GetAll => table.run()
    case ById(id) => findById(id).run()
    case ByFirstNameAndSureName(firstName, sureName) => findByFirstNameAndSureName(firstName, sureName).run()
  }
}
