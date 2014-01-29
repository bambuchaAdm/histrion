package org.virtuslab.histrion

import org.scalatest.fixture
import akka.actor.{Props, Actor}
import scala.slick.driver.H2Driver.simple._

class TestQueryActor(val table: TableQuery[TestTable], val executor: QueryExecutor) extends QueryActor {

  val query = table

  def receive: Actor.Receive = {
    case _ => query.run()
  }
}

class QueryActorTest extends fixture.FlatSpec with ActorTestKit with DatabaseFixture
{
  behavior of classOf[QueryActor].getSimpleName

  it should "can run query" in { database =>
    val executor = new QueryExecutor(database.database)
    val actor = system.actorOf(Props(classOf[TestQueryActor], database.test, executor))
    actor ! "test"
    expectMsg(Seq((1,1)))
  }
}
