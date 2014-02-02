package org.virtuslab.histrion

import org.scalatest.fixture
import java.util.concurrent.Executors
import akka.dispatch.ExecutionContexts
import akka.actor.Props

/**
 * Created by bambucha on 02.02.14.
 */
class BigQueryActorIntegrationTest extends fixture.FlatSpec with ActorTestKit with DatabaseFixture {

  behavior of classOf[BigQueryActor].getSimpleName

  val executionContext = ExecutionContexts.global()

  it should "return Vector of Persons after querying for all" in { database =>
    val queryExecutor = new QueryExecutor(database.database, executionContext)
    val bigQueryActor = system.actorOf(Props(classOf[BigQueryActor]))
  }
}
