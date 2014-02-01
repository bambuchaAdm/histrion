package org.virtuslab.histrion

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{BeforeAndAfterAll, fixture, Outcome}
import java.io.File

case class TestEntity(id: Int, value: Int)

class TestTable(tag: Tag) extends Table[(Int,Int)](tag,"test") {

  def id = column[Int]("id")

  def value = column[Int]("value")

  def * = (id,value)
}

class TestDatabase(id: Int){

  val database = Database.forURL(s"jdbc:h2:mem:$id", driver = "org.h2.Driver")

  var connection = database.createConnection() // Persists memory database between sessions

  def init() : Unit = {
    database.withSession{ implicit s =>
      test.ddl.create
      all.foreach(x => test += x)
    }
  }

  def close() : Unit = {
    connection.close()
  }

  val all = Vector((1,1),(2,10),(3,42))

  val test = TableQuery[TestTable]

  def withTransaction[T](f : Session => T) : T = database.withTransaction(f)

  def withSession[T](f: Session => T) : T = database.withSession(f)
}

object TestDatabase {
  var counter = 0
  def get : TestDatabase = {
    counter += 1
    new TestDatabase(counter)
  }
}

trait DatabaseFixture extends BeforeAndAfterAll { self : fixture.FlatSpec =>


  type FixtureParam = TestDatabase

  protected def withFixture(test: OneArgTest): Outcome = {
    val database = TestDatabase.get
    try {
      database.init()
      test.apply(database)
    } finally {
      database.close()
    }
  }
}

