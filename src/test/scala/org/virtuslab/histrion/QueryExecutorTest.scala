package org.virtuslab.histrion

import org.scalatest.{Outcome, FlatSpec, Matchers}
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

  val executionContext = mock[ExecutionContext]
  val database = mock[Database]
  val queryExecutor = new QueryExecutor(database, executionContext)

  override protected def withFixture(test: NoArgTest): Outcome = {
    reset(executionContext, database)
    test()
  }

  it should "schedule select future on  select query" in {
    val query = mock[Query[Table[_], _]]
    queryExecutor.scheduleSelect(query)
    verify(executionContext).execute(isA(classOf[SelectFuture[_, _]]))
  }

  it should "schedule delete future on delete query" in {
    val query = mock[Query[Table[_], _]]
    queryExecutor.scheduleDelete(query)
    verify(executionContext).execute(isA(classOf[DeleteFuture[_, _]]))
  }

  it should "schedule update future on update query" in {
    val updateQuery = mock[Query[Column[Int], Int]]
    queryExecutor.scheduleUpdate(updateQuery,5)
    verify(executionContext).execute(isA(classOf[UpdateFuture[_, _]]))
  }


}
