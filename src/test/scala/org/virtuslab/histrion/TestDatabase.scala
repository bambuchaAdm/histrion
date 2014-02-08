package org.virtuslab.histrion

import scala.slick.driver.H2Driver.simple._
import org.scalatest.{BeforeAndAfterAll, fixture, Outcome}
import org.slf4j.LoggerFactory
import scala.util.Random
import java.util.concurrent.atomic.AtomicInteger

case class TestEntity(id: Int, value: Int)

class TestTable(tag: Tag) extends Table[(Int,Int)](tag,"test") {

  def id = column[Int]("id")

  def value = column[Int]("value")

  def * = (id,value)
}

case class Person(id: Option[Int], nick: String, sureName: String, firstName: String, age: Int)

class BigTestTable(tag: Tag) extends Table[Person](tag, "persons") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("firstname")

  def sureName = column[String]("surename")

  def nick = column[String]("nick")

  def age = column[Int]("age")

  def * = (id.?, nick, sureName, firstName, age) <> (Person.tupled, Person.unapply)
}

class TestDatabase(id: Int){

  val database = Database.forURL(s"jdbc:h2:mem:$id", driver = "org.h2.Driver")

  val connection = database.createConnection() // Persists memory database between sessions

  def init() : Unit = {
    database.withSession{ implicit s =>
      test.ddl.create
      personTest.ddl.create
      all.foreach(x => test += x)
      persons.foreach(person => personTest += person)
    }
  }

  def close() : Unit = {
    connection.close()
  }

  val all = Vector((1,1),(2,10),(3,42))

  val persons = Vector(Person(Some(1), "Profesor Kleks", "Ambroży", "Kleks", 100),
                       Person(Some(2), "Golarz Filip", "Filip", "Golarz", 3))

  val test = TableQuery[TestTable]

  val personTest = TableQuery[BigTestTable]

  def withTransaction[T](f : Session => T) : T = database.withTransaction(f)

  def withSession[T](f: Session => T) : T = database.withSession(f)
}

object TestDatabase {
  var counter = new AtomicInteger(Random.nextInt(1500))
  def get : TestDatabase = {
    LoggerFactory.getLogger(classOf[TestDatabase]).info("Next database {}",counter)
    val result = new TestDatabase(counter.getAndIncrement)
    result.init()
    result
  }
}

trait DatabaseFixture extends BeforeAndAfterAll { self : fixture.FlatSpec =>


  type FixtureParam = TestDatabase

  protected def withFixture(test: OneArgTest): Outcome = {
    val database = TestDatabase.get
    try {
      test.apply(database)
    } finally {
      database.close()
    }
  }
}

