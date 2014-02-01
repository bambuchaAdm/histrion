package org.virtuslab.histrion

import scala.concurrent.Promise
import scala.slick.driver.H2Driver.simple._

/**
 * Created by bambucha on 01.02.14.
 */
class UpdateFuture[A,B](session: Session, query: Query[A,B], value: B) extends Runnable{
  val promise = Promise[Int]()

  def run(): Unit = {
    try {
      promise.success(query.update(value)(session))
    } catch {
      case t: Throwable => promise.failure(t)
    }
  }
}
