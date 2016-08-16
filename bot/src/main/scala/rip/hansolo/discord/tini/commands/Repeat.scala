package rip.hansolo.discord.tini.commands
import monix.eval.Task
import monix.execution.CancelableFuture
import monix.execution.atomic.Atomic
import net.dv8tion.jda.entities.Message

import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global
import rip.hansolo.discord.tini.brain.{TextBrainRegion, TiniBrain}

import scala.collection.mutable.ListBuffer

/**
  * Created by: 
  *
  * @author Raphael
  * @version 16.08.2016
  */
object Repeat extends Command {
  override def prefix: String = "!repeat"
  private val repeatTasks = new ListBuffer[CancelableFuture[Unit]]

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    val arg = args.split(" ")


    //TODO: make it pretty
    if( arg.length >= 2 ) {
      val count = Atomic(arg(0).toInt - 1)

      val repTask = Task {
        arg(1) match {
          case "image" => DriveImage.exec(arg.drop(2).mkString(" "), message)
          case "catfacts" => Catfacts.exec(arg.drop(2).mkString(" "), message)
          case "be" => Imitate.exec(arg.drop(2).mkString(" "), message)
          case "roll" => Roll.exec(arg.drop(2).mkString(" "), message)
        }
      }

      repTask.runAsync
      repeatTasks += repTask.delayExecution(10 seconds)
                            .restartUntil((Unit) => { count.getAndDecrement(1) < 0 })
                            .runAsync

    } else {
      message.getChannel.sendMessageAsync("Usage: !repeat <count> <command> <other-command-args>", null)
    }
  }

  def shutup() = repeatTasks foreach { _.cancel() }
}
