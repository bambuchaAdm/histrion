package org.virtuslab.histrion

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{fixture, Outcome, Matchers}

case class TestEntity(id: Int, value: Int)

class TestTable(tag: Tag) extends Table[(Int,Int)](tag,"test") {

  def id = column[Int]("id")

  def value = column[Int]("value")

  def * = (id,value)
}

class TestDatabase(database: Database){

  def this(id:Int) = {
    this(Database.forURL("jdbc:h2:mem:test"+id, driver = "org.h2.Driver"))
    database.withSession(s => test.ddl.create(s))
  }

  val test = TableQuery[TestTable]

  def withTransaction[T](f : (Session) => T) = database.withTransaction(f)
}

object TestDatabase {
  var counter = 0
  def get : TestDatabase = {
    counter += 1
    new TestDatabase(counter)
  }
}

trait DatabaseFixture { self : fixture.FlatSpec =>

  protected val database = TestDatabase.get

  type FixtureParam = Session

  protected def withFixture(test: OneArgTest): Outcome = {
    database.withTransaction(session => test.apply(session))
  }
}

