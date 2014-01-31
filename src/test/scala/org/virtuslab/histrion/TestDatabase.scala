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

class TestDatabase(id: Int, val file: File){

  val database = Database.forURL(s"jdbc:h2:${file.getAbsolutePath}${id}", driver = "org.h2.Driver")

  def this(id:Int) =  {
    this(id, File.createTempFile("histrion",""))
    init()
  }

  def init() : Unit = {
    database.withSession{ implicit s =>
      test.ddl.create
      all.foreach(x => test += x)
    }
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
    val result  = test.apply(database)
    database.file.delete()
    result
  }
}

