package rip.hansolo.discord.tini.brain


import scala.concurrent.Promise

import monix.execution.atomic.Atomic


/**
  * Contains the state of Tini's Brain
  */
object TiniBrain {
  def killYourself() = prophecy.success()

  /**
    * If this promise is fulfilled, Tini will kill itself and take the JVM with her
    */
  val prophecy = Promise[Unit]
  val is8ball = Atomic(true)
}
