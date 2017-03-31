package science.wasabi.tini.bot.helloworld


import akka.typed.ScalaDSL._
import akka.typed._


object HelloWorld {

  def go(): Unit = {
    final case class Greet(whom: String)
    final case class Greeted(whom: String)

    val greeter = Static[Greet] { msg =>
      println(s"Hello ${msg.whom}!")

    }

    val system: ActorSystem[Greet] = ActorSystem("hello", greeter)

    system ! Greet("world")

    for {
      x <- Some("lol")
    }
      println("system terminated")
  }

}