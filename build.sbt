organization := "org.virtuslab"

name := "slick3-test"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += Resolver.typesafeRepo("releases")

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.0.4-SNAPSHOT",
  "com.typesafe" % "config" % "1.3.0",
  "com.zaxxer" % "HikariCP" % "2.4.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "com.h2database" % "h2" % "1.4.189" //% "test"
)
