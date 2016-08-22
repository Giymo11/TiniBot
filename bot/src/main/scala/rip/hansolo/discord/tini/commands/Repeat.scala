package rip.hansolo.discord.tini.commands
import cats.data.Xor
import monix.eval.Task
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import monix.execution.atomic.Atomic
import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.brain.TextBrainRegion

import scala.collection.concurrent.TrieMap
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

  private val repeatTasks = new TrieMap[String,ListBuffer[CancelableFuture[Unit]]]()
  private val defaultDuration = 15

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    val arguments = args.split(" ")

    if( arguments.length >= 2 ) {
      val count = Atomic(arguments.head.toInt - 1)
      val duration = Xor.catchNonFatal(if (arguments(1).toInt > defaultDuration) arguments(1).toInt else defaultDuration ).toOption

      val cmdStart = if( duration.isEmpty ) 1 else 2

      val repTask = Task {
        val cmd = TextBrainRegion.channelCommands.get(arguments(cmdStart).drop(1))
        cmd.getOrElse(NotACommand).exec(arguments.drop(cmdStart+1).mkString(" "), message)
      }

      message.getChannel.sendMessageAsync(s"Tini will repeat `${arguments.drop(cmdStart).mkString(" ")}` " +
        s"***${count.get}*** times and every ***${duration.getOrElse(defaultDuration) minutes}***",null)

      repTask.runAsync
      val tasks  = repeatTasks.getOrElseUpdate(message.getChannelId,new ListBuffer[CancelableFuture[Unit]])
      val future = repTask.delayExecution(duration.getOrElse(defaultDuration) minutes)
                          .restartUntil((Unit) => { count.getAndDecrement(1) == 1 } ) // because this gets the value before updating it
                          .runAsync

      future andThen { case _ => repeatTasks(message.getChannelId) -= future } // can't use onFinish from Task cuz variable is not fully set
      tasks += future
    } else {
      message.getChannel.sendMessageAsync("Usage: " + longHelp, null)
    }

  }

  def stopRepeat(channelID: String): Unit = repeatTasks.get(channelID) match {
    case Some(channel) => channel foreach { _.cancel() }
    case _ =>
  }
  def shutup() = repeatTasks foreach { _._2 foreach { _.cancel() } }

  override def longHelp: String = s"`$command <count> [timeout] <command> <command-args>` - Repeat the command multiple times with the arguments"
  override def shortHelp: String =  s"`$command` - the bot will repeat some messages"
}
