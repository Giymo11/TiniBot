
import sbt.Keys._
import sbt.Project.projectToRef

//enablePlugins(DockerPlugin)

version := "1.0-SNAPSHOT"

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "jitpack.io" at "https://jitpack.io"
resolvers += Resolver.jcenterRepo

val scalaV = "2.11.8"

lazy val shared = project.in(file("shared"))
  .settings(
    scalaVersion := scalaV,
    name := "shared",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats" % "0.6.1",
      "io.monix" %% "monix" % "2.0-RC9",
      "io.monix" %% "monix-cats" % "2.0-RC9",
      "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0-RC3"
    )
  )

lazy val bot = project.in(file("bot"))
  .settings(
    scalaVersion := scalaV,
    name := "bot",
    libraryDependencies ++= Seq(
      "com.github.austinv11" % "Discord4j" % "2.5.2",
      "net.dv8tion" % "JDA" % "2.2.0_334",
      "com.lihaoyi" %% "ammonite-ops" % "0.7.0"
    ),
    // TODO: add docker
    mainClass in assembly := Some("rip.hansolo.discord.tini.MainJDA"),
    assemblyJarName in assembly := "TiniBot.jar",
    assemblyMergeStrategy in assembly := {
      case PathList(xs @ _*) if xs.contains("opuswrapper") || xs.contains("tritonus") => MergeStrategy.last // needed to have both JDA and D4J at the same time
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
  .dependsOn(shared)
  // a alternative to this would be the sbt-native plugin
  .enablePlugins(DockerPlugin)
  .settings(
    dockerfile in docker := {
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"
      new Dockerfile {
        from("java:8")
        add(artifact, artifactTargetPath)
        entryPoint("java", "-jar", artifactTargetPath)
      }
    },
    imageNames in docker := Seq(
      ImageName("giymo11/tinibot:latest")
    )
  )

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
