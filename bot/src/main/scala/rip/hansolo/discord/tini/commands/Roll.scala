package rip.hansolo.discord.tini.commands


import scala.util.Random

import rip.hansolo.discord.tini.Util._
import rip.hansolo.discord.tini.resources.ShitTiniSays


/**
  * The class responsible for the `!roll` command.
  */
object Roll {
  /**
    * Check if the command was a roll and returns the result if applicable
    * @return Some(result) of the roll if the argument was for a roll, or None if the argument was not for a roll
    */
  def unapply(arg: String): Option[String] = arg match {
    case rollCommand if rollCommand.startsWith("!roll") =>
      Some(rollTheDice(rollCommand))
    case _ => None
  }

  def rollTheDice(command: String): String = {
    val args = command.trim.split(" ").toList

    val result = args match {
      case _ :: dice :: Nil if dice.contains("d") =>
        getResultsForDiceSyntax(dice)
      case _ :: StringInt(upper) :: Nil =>
        Some(s"**${oneOf(1 to upper: _*)}**")
      case _ :: StringInt(lower) :: StringInt(upper) :: Nil =>
        Some(s"**${oneOf(lower to upper: _*)}**")
      case _ =>
        None
    }

    result match {
      case Some(value) => ShitTiniSays.rollAnnouncement + value
      case None => ShitTiniSays.rollUsage
    }
  }

  /**
    * @param dice A string of the form <count>d<sides>, for example `2d6`
    * @return The resulting String
    */
  private[this] def getResultsForDiceSyntax(dice: String) = {
    val rolls = dice.split("d").toList match {
      case StringInt(count) :: StringInt(sides) :: Nil => for(i <- 1 to count) yield Random.nextInt(sides) + 1
      case _ => Seq.empty
    }
    rolls match{
      case Nil => None
      case roll :: Nil => Some(s"**$roll**")
      case _ => Some(s"( ${rolls.mkString(" + ")} ) = **${rolls.sum}**")
    }
  }
}