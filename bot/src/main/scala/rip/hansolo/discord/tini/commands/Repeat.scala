package rip.hansolo.discord.tini.commands


import scala.concurrent.duration._
import scala.language.postfixOps

import cats.data.Xor

import rip.hansolo.discord.tini.Util._
import rip.hansolo.discord.tini.brain._
import rip.hansolo.discord.tini.resources._


/**
  * Created by: 
  *
  * @author Raphael
  * @version 16.08.2016
  */
object Repeat extends Command {

  override def prefix: String = "repeat"

  // TODO: save this kinda stuff on the firebase.
  private def minimumDuration(implicit brain: LocalSettings) = brain.minimumRepeatDurationMins

  // TODO: re-use more of TextBrainRegion
  def charsToDrop(implicit brain: LocalSettings): Int = brain.tiniPrefix.length

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: MessageData)(implicit brain: LocalSettings): Unit = {
    val arguments = args.dropWhile(isWhitespace).split(" ")
    println("Prefix: " + arguments.head)

    if( arguments.length >= 2 ) {
      val count = arguments(0).toInt - 1

      val originalDuration = Xor.catchNonFatal(arguments(1).toInt).toOption

      val (duration, numberOfParameters, wasRescheduled) = if(originalDuration.isEmpty)
        (minimumDuration, 1, false)
      else if(originalDuration.get >= minimumDuration)
        (originalDuration.get, 2, false)
      else
        (minimumDuration, 2, true)

      val toRepeat = arguments.drop(numberOfParameters)
        .mkString(" ").dropWhile(isWhitespace)

      message.getChannel.sendMessageAsync(s"Tini will repeat `$toRepeat` " +
        s"***${count + 1}*** times and every ***${duration minutes}***" +
        (if(wasRescheduled) " (rescheduled to minimum duration)" else ""), null)

      RepeatBrain.startRepeatTask(toRepeat, message, count, duration, "")
    } else
      message.getChannel.sendMessageAsync("Usage: " + longHelp, null)
  }

}
