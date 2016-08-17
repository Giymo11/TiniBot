package rip.hansolo.discord.tini.commands


import scala.language.postfixOps
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

import cats.data.Xor

import monix.eval.Task
import monix.execution.CancelableFuture
import monix.execution.atomic.Atomic
import monix.execution.Scheduler.Implicits.global

import net.dv8tion.jda.entities.Message

import rip.hansolo.discord.tini.resources.ShitTiniSays


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
    val arguments = args.split(" ")

    //TODO: make it pretty
    if( arguments.length >= 2 ) {
      val count = Atomic(arguments(0).toInt - 1)
      val duration = Xor.catchNonFatal(arguments(1).toInt-1).toOption

      val cmdStart = if( duration.isEmpty ) 1 else 2

      val payload = arguments.drop(cmdStart).mkString(" ")
      println("Payload: " + payload)

      val repTask = Task[Unit] {
        payload match {
          case Command(myCommand, commandArgs) =>
            myCommand.exec(commandArgs, message)
          case _ =>
            message.getChannel.sendMessage(ShitTiniSays.help)
        }
      }

      repTask.runAsync
      repeatTasks += repTask.delayExecution(duration.getOrElse(10) seconds)
                            .restartUntil((Unit) => { count.getAndDecrement(1) == 1 } ) // because this gets the value before updating it
                            .runAsync

    } else {
      message.getChannel.sendMessageAsync("Usage: !repeat <count> [timeout] <command> <other-command-args>", null)
    }

  }

  def shutup() = repeatTasks foreach { _.cancel() }
}
