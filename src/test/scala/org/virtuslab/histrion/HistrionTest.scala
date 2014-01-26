package org.virtuslab.histrion

import org.scalatest.{Matchers, fixture}

import akka.actor.{Props, Actor}
import scala.slick.driver.H2Driver.simple._

case object GetAll



class TestRepository(val database: TestDatabase) extends Actor {

  val allQuery = database.test

  def receive: Actor.Receive = {
    case GetAll => sender ! database.withSession(allQuery.list()(_))
  }
}

class HistrionTest extends fixture.FlatSpec with DatabaseFixture with ActorTestKit with Matchers {

  behavior of "Histrion"

  val data = (1,1)

  it should "run query after recived message" in { database: TestDatabase =>
    val testRepository =  system.actorOf(Props(classOf[TestRepository], database))
    testRepository ! GetAll
    expectMsg(Iterable(data))
  }
}
