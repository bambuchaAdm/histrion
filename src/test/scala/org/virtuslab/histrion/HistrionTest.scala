package org.virtuslab.histrion

import org.scalatest.{Matchers, fixture}
import scala.slick.driver.H2Driver.simple._


class HistrionTest extends fixture.FlatSpec with DatabaseFixture with ActorTestKit with Matchers {

  behavior of "Historion"

  it should "run query in execution context" in { session: Session =>

  }
}
