package org.virtuslab.histrion

import org.scalatest.fixture
import akka.actor.{ActorRef, Props, Actor}
import scala.slick.lifted.{Query, TableQuery}

object TestExecutor extends QueryExecutor {
  override def schedule(query: Query[_, _], recipient: ActorRef): Unit = recipient ! (1,1)
}

class TestQueryActor(val table: TableQuery[TestTable],val executor: QueryExecutor) extends QueryActor {

  val query = table

  def receive: Actor.Receive = {
    case _ => query.run
  }
}

class QueryActorTest extends fixture.FlatSpec with ActorTestKit with DatabaseFixture
{
  behavior of classOf[QueryActor].getSimpleName

  it should "can run query" in { database =>
    val actor = system.actorOf(Props(classOf[TestQueryActor],database.test, TestExecutor))
    actor ! "test"
    expectMsg((1,1))
  }
}
