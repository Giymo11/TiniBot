package rip.hansolo.discord.tini.commands
import cats.data.Xor
import monix.eval.Task
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import monix.execution.atomic.Atomic
import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.brain.TextBrainRegion

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 16.08.2016
  */
object Repeat extends Command {

  override def prefix: String = "repeat"
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
      val count = Atomic(arguments.head.toInt - 1)
      val duration = Xor.catchNonFatal(arguments(1).toInt).toOption

      val cmdStart = if( duration.isEmpty ) 1 else 2

      val repTask = Task {
        val cmd = TextBrainRegion.channelCommands.get(arguments(cmdStart))
        cmd.getOrElse(NotACommand).exec(arguments.drop(cmdStart+1).mkString(" "), message)
      }

      repTask.runAsync
      repeatTasks += repTask.delayExecution(duration.getOrElse(10) seconds)
                            .restartUntil((Unit) => { count.getAndDecrement(1) == 1 } ) // because this gets the value before updating it
                            .runAsync

    } else {
      message.getChannel.sendMessageAsync("Usage: " + longHelp, null)
    }

  }

  def shutup() = repeatTasks foreach { _.cancel() }

  override def longHelp: String = s"`$command <count> [timeout] <command> <command-args>` - Repeat the command multiple times with the arguments"
  override def shortHelp: String =  s"`$command` - the bot will repeat some messages"
}
