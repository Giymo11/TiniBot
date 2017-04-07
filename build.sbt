
import sbt.Keys._
import sbt.Project.projectToRef



cancelable in Global := true


name := "tini-project"

version := "0.0.1"

scalaVersion := "2.12.1"


//resolvers += "jitpack.io" at "https://jitpack.io"
resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "jcenter-bintray" at "http://jcenter.bintray.com",
  "twttr" at "https://maven.twttr.com/",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)

val akkaV = "2.4.17"
val akkaHttpV = "10.0.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-typed-experimental" % akkaV,
  "com.typesafe.akka" %% "akka-cluster" % akkaV,
  "com.typesafe.akka" %% "akka-remote" % akkaV,
  "com.typesafe.akka" %% "akka-persistence" % akkaV,
  "com.typesafe.akka" %% "akka-persistence-tck" % akkaV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaV,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaV,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaV,

  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-xml" % akkaHttpV
)

libraryDependencies ++= Seq(
  "net.dv8tion" % "JDA" % "3.0.0_157"
)
