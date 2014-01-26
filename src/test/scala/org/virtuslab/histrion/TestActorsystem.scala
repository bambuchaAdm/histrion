package org.virtuslab.histrion

import akka.testkit.{ImplicitSender, TestKitBase}
import org.scalatest.Suite
import akka.actor.ActorSystem

/**
 * Created by bambucha on 26.01.14.
 */
trait TestActorSystem { self: Suite =>
  implicit val system: ActorSystem = ActorSystem.create("xxx")
}

trait ActorTestKit extends TestActorSystem with TestKitBase {
  self: Suite =>
  implicit val sender = testActor
}
