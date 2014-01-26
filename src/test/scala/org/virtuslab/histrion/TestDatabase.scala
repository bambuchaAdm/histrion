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

class TestDatabase(private val id: Int, file: File){

  val database = Database.forURL(s"jdbc:h2:${file.getAbsolutePath}${id}", driver = "org.h2.Driver")

  def this(id:Int) =  {
    this(id, File.createTempFile("histrion",""))
    database.withSession{ implicit s =>
      test.ddl.create
      test += (1,1)
    }
  }

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

  protected val database = TestDatabase.get

  type FixtureParam = TestDatabase

  protected def withFixture(test: OneArgTest): Outcome = {
    test.apply(database)
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
  }
}

