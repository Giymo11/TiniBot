package science.wasabi.tini.bot.helloworld


import akka.util.Timeout

import scala.concurrent.Future

object HelloWorldTyped {
  import akka.typed.ScalaDSL.Static
  import akka.typed.ActorRef

  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String)

  val greeter = Static[Greet] { msg =>
    println(s"Hello ${msg.whom}!")
    msg.replyTo ! Greeted(msg.whom)
  }
}



import akka.actor.Actor

object Greeter {
  case class Greet(whom: String)
  case class Greeted(whom: String)
}

class Greeter extends Actor {
  def receive = {
    case msg: Greeter.Greet =>
      println(s"Hello ${msg.whom}!")
      sender() ! Greeter.Greeted(msg.whom)
  }
}



object Main extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._
  implicit val timeout = Timeout(5 seconds)

  goTyped()
  goUntyped()

  def goTyped(): Unit = {
    import HelloWorldTyped._
    import akka.typed.ActorSystem
    import akka.typed.AskPattern._

    val system: ActorSystem[Greet] = ActorSystem("hello", greeter)
    implicit val scheduler = system.scheduler

    val future: Future[Greeted] = system ? (Greet("typed world", _))

    for {
      greeting <- future.recover {
        case ex => ex.getMessage
      }
      done <- {
        println(s"result: $greeting");
        system.terminate()
      }
    } println("system terminated")
  }

  def goUntyped(): Unit = {
    import akka.actor.{ActorSystem, Props}
    import Greeter._
    import akka.pattern.ask

    val system = ActorSystem("helloSystem")

    val actor = system.actorOf(Props[Greeter], "hello")

    val future = actor ? Greet("untyped world")

    for {
      greeting <- future.recover {
        case ex => ex.getMessage
      }
      done <- {
        println(s"result: $greeting");
        system.terminate()
      }
    } println("system terminated")
  }
}