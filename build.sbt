
import sbt.Keys._
import sbt.Project.projectToRef

version := "1.0-SNAPSHOT"

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "jitpack.io" at "https://jitpack.io"

val scalaV = "2.11.8"



lazy val shared = project.in(file("shared"))
  .settings(
    scalaVersion := scalaV,
    name := "shared",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats" % "0.6.1",
      "io.monix" %% "monix" % "2.0-RC9",
      "io.monix" %% "monix-cats" % "2.0-RC9"
    )
  )

lazy val bot = project.in(file("bot"))
  .settings(
    scalaVersion := scalaV,
    name := "bot",
    libraryDependencies ++= Seq(
      "com.github.austinv11" % "Discord4j" % "2.5.2"
    ),
    mainClass in assembly := Some("rip.hansolo.discord.tini.Main"),
    assemblyJarName in assembly := "TiniBot.jar"
  ).dependsOn(shared)

lazy val web = project.in(file("web"))
  .settings(
    scalaVersion := scalaV,
    name := "web"
  ).dependsOn(shared)

lazy val root = project.in(file("."))
  .aggregate(shared, bot, web)
  .settings(
    scalaVersion := scalaV,
    name := "root"
  )
