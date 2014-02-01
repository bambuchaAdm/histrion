package org.virtuslab.histrion

import scala.slick.lifted.Query
import scala.concurrent.{ExecutionContext, Future}
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory
import scala.slick.driver.H2Driver.simple._

/**
 * Created by Łukasz Dubiel.
 */
class QueryExecutor(database: Database, context: ExecutionContext) extends ExecutionContext {

  val logger = LoggerFactory.getLogger(s"Executor ${database.toString}")

  val session = database.createSession()

  def scheduleSelect[A, B](query: Query[A, B]) : Future[Seq[B]] = {
    val queryResolver = new SelectFuture(session,query)
    context.execute(queryResolver)
    queryResolver.promise.future
  }

  def scheduleDelete[A <: Table[_], B](query: Query[A, B]) : Future[Int] = {
    val deletionFuture = new DeleteFuture(session, query)
    context.execute(deletionFuture)
    deletionFuture.promise.future
  }

  def scheduleUpdate[A,B](query: Query[A,B], value: B) : Future[Int] = {
    val updateFuture = new UpdateFuture(session, query, value)
    context.execute(updateFuture)
    updateFuture.promise.future
  }

  def execute(runnable: Runnable): Unit = context.execute(runnable)

  def reportFailure(t: Throwable): Unit = context.reportFailure(t)
}


