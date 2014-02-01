name := "histrion"

organization := "org.virtuslab"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "com.typesafe.slick" %% "slick" % "2.0.0",
  "ch.qos.logback" % "logback-classic" % "1.0.3"
  )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test",
  "com.h2database" % "h2" % "1.3.175" % "test",
  "org.mockito" % "mockito-core" % "1.8.5" % "test"
  )

shellPrompt := { state => name.value + "> " }
