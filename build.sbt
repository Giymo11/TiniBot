
import sbt.Keys._
import sbt.Project.projectToRef

//enablePlugins(DockerPlugin)

version := "1.0-SNAPSHOT"

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
//resolvers += "jitpack.io" at "https://jitpack.io"
resolvers += Resolver.jcenterRepo
resolvers += "twttr" at "https://maven.twttr.com/"

lazy val markovLib = uri("https://github.com/runarorama/Malakov.git")
val scalaV = "2.11.8"

lazy val sharedJVM = project.in(file("shared"))
  .settings(
    scalaVersion := scalaV,
    name := "shared",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats" % "0.6.1",
      "io.monix" %% "monix" % "2.0-RC9",
      "io.monix" %% "monix-cats" % "2.0-RC9",
      "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0-RC3",
      "com.google.firebase" % "firebase-server-sdk" % "3.0.1",
      "org.scalactic" %% "scalactic" % "3.0.0"
    )
  )

lazy val bot = project.in(file("bot"))
  .settings(
    scalaVersion := scalaV,
    name := "bot",
    resolvers += Resolver.jcenterRepo,
    libraryDependencies ++= Seq(
      "net.dv8tion" % "JDA" % "2.2.0_334",
      "com.lihaoyi" %% "ammonite-ops" % "0.7.0",
      "com.google.apis" % "google-api-services-drive" % "v3-rev37-1.22.0",
      "com.github.pathikrit" %% "better-files" % "2.16.0",
      "org.scalatest" %% "scalatest" % "3.0.0" % Test,
      "com.mashape.unirest" % "unirest-java" % "unirest-java",
      "com.github.kxbmap" %% "configs" % "0.4.2",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.5"
    ),

    mainClass in assembly := Some("rip.hansolo.discord.tini.Main"),
    assemblyJarName in assembly := "TiniBot.jar",
    assemblyMergeStrategy in assembly := {
      case PathList(xs @ _*) if xs.contains("opuswrapper") || xs.contains("tritonus") => MergeStrategy.last // needed to have both JDA and D4J at the same time
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
  .dependsOn(sharedJVM)
  .dependsOn(markovLib)
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
    name := "web",
    resolvers += "twttr" at "https://maven.twttr.com/",
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finch-core" % "0.11.0-M2",
      "com.github.finagle" %% "finch-circe" % "0.11.0-M2",
      "io.circe" %% "circe-generic" % "0.5.0-M2",
      "com.twitter" %% "twitter-server" % "1.21.0",
      "com.lihaoyi" %% "scalatags" % "0.6.0",
      "com.github.japgolly.scalacss" %% "core" % "0.4.1",
      "com.github.japgolly.scalacss" %% "ext-scalatags" % "0.4.1"
    ),

    mainClass in assembly := Some("rip.hansolo.discord.tiniweb.Main"),
    assemblyJarName in assembly := "TiniWeb.jar",
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList(xs @ _*) => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  ).dependsOn(sharedJVM)
  .enablePlugins(DockerPlugin)
  .settings(
    dockerfile in docker := {
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"
      new Dockerfile {
        from("java:8")
        add(artifact, artifactTargetPath)
        expose(80)
        entryPoint("java", "-jar", artifactTargetPath)
      }
    },
    imageNames in docker := Seq(
      ImageName("giymo11/tiniweb:latest")
    )
  )

lazy val root = project.in(file("."))
  .aggregate(sharedJVM, bot, web)
  .settings(
    scalaVersion := scalaV,
    name := "root"
  )
