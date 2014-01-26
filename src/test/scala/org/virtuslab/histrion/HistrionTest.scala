package org.virtuslab.histrion

import org.scalatest.{Matchers, fixture}

import akka.actor.{ActorRef, Props, Actor}
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory
import scala.slick.lifted.Query
import scala.slick.driver.H2Driver.simple._
import scala.collection.mutable
import scala.concurrent.Future

case object GetAll

class QueryExecutor(val database: TestDatabase) extends scala.concurrent.ExecutionContext {
  val executorService = Executors.newFixedThreadPool(4)

  val logger = LoggerFactory.getLogger("Executor")

  val queue = mutable.Queue

  def execute(runnable: Runnable): Unit = {
    executorService.submit(runnable)
  }

  def reportFailure(t: Throwable): Unit = logger.error("Error on query",t)

  def execute(query: Query[_,_]){
    Future{
      query.run(getSession)
    }.onComplete{
      case Success(v) =>
    }
  }

  def getSession = ???

}


class TestRepository(val executor: QueryExecutor) extends Actor {

  val database = executor.database

  val allQuery = database.test.map(x => (x.id,x.value))


  def receive: Actor.Receive = {
    case GetAll => allQuery.run
  }
}

class HistrionTest extends fixture.FlatSpec with DatabaseFixture with ActorTestKit with Matchers {

  behavior of "Histrion"

  val data = (1,1)

  it should "run query after recived message" in { database: TestDatabase =>
    val testRepository =  system.actorOf(Props(classOf[TestRepository],new QueryExecutor(database)))
    testRepository ! GetAll
    expectMsg(Iterable(data))
  }
}
