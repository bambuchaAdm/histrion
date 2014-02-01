package org.virtuslab.histrion

import org.scalatest.fixture
import akka.actor.{Props, Actor}
import scala.slick.driver.H2Driver.simple._

object GetAll

case class ById(id: Int)

case class ByValue(value: Int)

case class Delete(id: Int)

case class Update(id: Int, value: Int)

class TestQueryActor(val table: TableQuery[TestTable], val executor: QueryExecutor) extends QueryActor {

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

class QueryActorTest extends fixture.FlatSpec with ActorTestKit with DatabaseFixture
{
  behavior of classOf[QueryActor].getSimpleName



  it should "can run query" in { database =>
    val executor = new QueryExecutor(database.database)
    val props = Props(classOf[TestQueryActor], database.test, executor)
    val actor = system.actorOf(props)
    actor ! GetAll
    expectMsg(database.all)
  }

  it should "query by ID" in { database =>
    val executor = new QueryExecutor(database.database)
    val props = Props(classOf[TestQueryActor], database.test, executor)
    val actor = system.actorOf(props)
    val id: Int = 2
    val result = database.all.find(_._1 == id).get
    actor ! ById(id)
    expectMsg(Vector(result))
  }

  it should "query by Value" in { database =>
    val executor = new QueryExecutor(database.database)
    val props = Props(classOf[TestQueryActor], database.test, executor)
    val actor = system.actorOf(props)
    val value: Int = 42
    val expected = database.all.find(_._2 == value).get
    actor ! ByValue(value)
    expectMsg(Vector(expected))
  }

  it should "delete by value" in { database =>
    val executor = new QueryExecutor(database.database)
    val props = Props(classOf[TestQueryActor], database.test, executor)
    val actor = system.actorOf(props)
    val id: Int = 3
    actor ! Delete(id)
    expectMsg(1)
    actor ! GetAll
    expectMsg(database.all.filter(_._1 != id))
  }

  it should "update value using ID" in {database =>
    val executor = new QueryExecutor(database.database)
    val props = Props(classOf[TestQueryActor], database.test, executor)
    val actor = system.actorOf(props)
    val id = 3
    val newValue = 43
    actor ! Update(id,newValue)
    expectMsg(1)
    actor ! ById(id)
    expectMsg(Vector((id,newValue)))

  }
}
