package org.virtuslab.histrion

import scala.slick.driver.H2Driver.simple._
import scala.concurrent.Promise

class DeleteFuture[A <: Table[_], B](session: Session, query: Query[A, B]) extends Runnable {

  val promise = Promise[Int]()

  def run(): Unit = {
    try {
       promise.success(query.delete(session))
    } catch {
      case t: Throwable => promise.failure(t)
    }
  }
}
