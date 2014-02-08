package org.virtuslab.histrion

import akka.actor.Actor
import scala.slick.lifted.TableQuery


object BigQueryActorProtocol {
  object GetAll
  case class ById(id: Int)
  case class ByNick(nick: String)
  case class ByFirstNameAndSureName(first: String, second: String)
}

class BigQueryActor(table: TableQuery[BigTestTable], val executor: QueryExecutor) extends  QueryActor {
  import BigQueryActorProtocol._

  import slick.driver.H2Driver.simple._

  def receive: Actor.Receive = {
    case GetAll => table.run()
    case ById(id) => table.filter(_.id === id).run()
    case ByFirstNameAndSureName(firstName, sureName) => table.filter(row => row.firstName === firstName && row.sureName === sureName).run()
  }
}
