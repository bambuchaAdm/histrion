package org.virtuslab.histrion

import org.scalatest.fixture
import java.util.concurrent.Executors
import akka.dispatch.ExecutionContexts
import akka.actor.Props

/**
 * Created by bambucha on 02.02.14.
 */
class MappingQueryActorIntegrationTest extends fixture.FlatSpec with ActorTestKit with DatabaseFixture {

  behavior of classOf[MappingQueryActor].getSimpleName

  val executionContext = ExecutionContexts.global()

  import MappingQueryActorProtocol._

  it should "return Vector of Persons after querying for all" in { database =>
    val queryExecutor = new QueryExecutor(database.database, executionContext)
    val bigQueryActor = system.actorOf(Props(classOf[MappingQueryActor], database.personTest, queryExecutor))
    bigQueryActor ! GetAll
    expectMsg(database.persons)
  }

  it should "return Vector with person with selected ID" in { database =>
    val queryExecutor = new QueryExecutor(database.database, executionContext)
    val bigQueryActor = system.actorOf(Props(classOf[MappingQueryActor], database.personTest, queryExecutor))
    bigQueryActor ! ById(1)
    expectMsg(database.persons.filter(_.nick matches "Profesor.*"))
  }

  it should "return persons by first name and sure name" in { database =>
    val queryExecutor = new QueryExecutor(database.database, executionContext)
    val bigQueryActor = system.actorOf(Props(classOf[MappingQueryActor], database.personTest, queryExecutor))
    bigQueryActor ! ByFirstNameAndSureName("Ambroży", "Kleks")
    expectMsg(database.persons.filter(_.nick matches "Profesor.*"))
  }
}
