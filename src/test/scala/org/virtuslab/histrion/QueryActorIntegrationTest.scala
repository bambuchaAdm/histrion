package org.virtuslab.histrion

import org.scalatest.fixture
import akka.actor.Props
import akka.dispatch.ExecutionContexts


class QueryActorIntegrationTest extends fixture.FlatSpec with ActorTestKit with DatabaseFixture
{
  behavior of "QueryActor"

  import TestQueryActorProtocol._

  val executionContext = ExecutionContexts.global()

  it should "can run query" in { database =>
    val executor = new QueryExecutor(database.database, executionContext)
    val props = Props(classOf[TestQueryActor], database.test, executor)
    val actor = system.actorOf(props)
    actor ! GetAll
    expectMsg(database.all)
  }

  it should "query by ID" in { database =>
    val executor = new QueryExecutor(database.database, executionContext)
    val props = Props(classOf[TestQueryActor], database.test, executor)
    val actor = system.actorOf(props)
    val id: Int = 2
    val result = database.all.find(_._1 == id).get
    actor ! ById(id)
    expectMsg(Vector(result))
  }

  it should "query by Value" in { database =>
    val executor = new QueryExecutor(database.database, executionContext)
    val props = Props(classOf[TestQueryActor], database.test, executor)
    val actor = system.actorOf(props)
    val value: Int = 42
    val expected = database.all.find(_._2 == value).get
    actor ! ByValue(value)
    expectMsg(Vector(expected))
  }

  it should "delete by value" in { database =>
    val executor = new QueryExecutor(database.database, executionContext)
    val props = Props(classOf[TestQueryActor], database.test, executor)
    val actor = system.actorOf(props)
    val id: Int = 3
    actor ! Delete(id)
    expectMsg(1)
    actor ! GetAll
    expectMsg(database.all.filter(_._1 != id))
  }

  it should "update value using ID" in {database =>
    val executor = new QueryExecutor(database.database, executionContext)
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
