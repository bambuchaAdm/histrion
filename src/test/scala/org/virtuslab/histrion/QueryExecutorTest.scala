package org.virtuslab.histrion

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.mock.MockitoSugar
import scala.slick.driver.H2Driver.simple._
import scala.concurrent.ExecutionContext
import org.mockito.Mockito

/**
 * Created by bambucha on 01.02.14.
 */
class QueryExecutorTest extends FlatSpec with Matchers with MockitoSugar {

  behavior of classOf[QueryExecutor].getSimpleName

  import Mockito._
  import org.mockito.Matchers._

  it should "call execution context on scheduling query" in {
    val executionContext = mock[ExecutionContext]
    val query = mock[Query[_, _]]
    val queryExecutor = new QueryExecutor(mock[Database], executionContext)
    queryExecutor.scheduleSelect(query)
    verify(executionContext).execute(isA(classOf[SelectFuture[_, _]]))

  }

}
