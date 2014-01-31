package org.virtuslab.histrion

import scala.slick.driver.H2Driver.simple._
import scala.concurrent.Promise

class SelectFuture[A,B](val session: Session, val query: Query[A,B]) extends Runnable {
  val promise = Promise[Seq[B]]()

  def run(): Unit = {
    try {
      promise.success(query.run(session))
    } catch {
      case e : Throwable => promise.failure(e)
    }
  }
}
