package org.virtuslab.histrion

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.mock.MockitoSugar
import scala.slick.driver.H2Driver.simple._
import scala.concurrent.ExecutionContext

/**
 * Created by bambucha on 01.02.14.
 */
class QueryExecutorTest extends FlatSpec with Matchers with MockitoSugar {

  behavior of classOf[QueryExecutor].getSimpleName

  it should "call execution context on scheduling query" in {
    val queryExecutor = new QueryExecutor(mock[Database],mock[ExecutionContext])
  }

}
