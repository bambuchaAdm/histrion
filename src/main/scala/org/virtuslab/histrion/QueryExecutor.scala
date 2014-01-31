package org.virtuslab.histrion

import scala.slick.lifted.Query
import scala.concurrent.{ExecutionContext, Future}
import scala.slick.driver.H2Driver.simple._
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory

/**
 * Created by Łukasz Dubiel.
 */
class QueryExecutor(database: Database) extends ExecutionContext {

  val logger = LoggerFactory.getLogger(s"Executor ${database.toString}")

  val threadExecutor = Executors.newSingleThreadExecutor()
  val session = database.createSession()

  def scheduleSelect[A, B](query: Query[A, B]) : Future[Seq[B]] = {
    val queryResolver = new SelectFuture(session,query)
    threadExecutor.submit(queryResolver)
    queryResolver.promise.future
  }

  def scheduleDelete[A <: Table[_], B](query: Query[A, B]) : Future[Int] = {
    val deletionFuture = new DeleteFuture(session, query)
    threadExecutor.submit(deletionFuture)
    deletionFuture.promise.future
  }

  def execute(runnable: Runnable): Unit = threadExecutor.submit(runnable)

  def reportFailure(t: Throwable): Unit = logger.error("Error in database execution context", t)
}


