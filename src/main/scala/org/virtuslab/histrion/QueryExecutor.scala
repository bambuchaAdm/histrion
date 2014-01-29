package org.virtuslab.histrion

import scala.slick.lifted.Query
import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.slick.driver.H2Driver.simple._
import java.util.concurrent.Executors
import org.slf4j.{LoggerFactory, Logger}

/**
 * Created by Łukasz Dubiel.
 */
class QueryExecutor(database: Database) extends ExecutionContext {

  val logger = LoggerFactory.getLogger(s"Executor ${database.toString}")

  val threadExecutor = Executors.newSingleThreadExecutor()
  val session = database.createSession()

  def schedule[A,B](query: Query[A, B]) : Future[Seq[B]] = {
    val queryResolver = new QueryFuture(session,query)
    threadExecutor.submit(queryResolver)
    queryResolver.promise.future
  }

  def execute(runnable: Runnable): Unit = threadExecutor.submit(runnable)

  def reportFailure(t: Throwable): Unit = logger.error("Error in database execution context", t)
}

class QueryFuture[A,B](val session: Session, val query: Query[A,B]) extends Runnable {
  val promise = Promise[Seq[B]]()

  def run(): Unit = {
    try {
      promise.success(query.run(session))
    } catch {
      case e : Throwable => promise.failure(e)
    }
  }
}

