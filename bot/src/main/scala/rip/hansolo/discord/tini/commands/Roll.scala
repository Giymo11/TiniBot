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
    case rollCommand if rollCommand.startsWith("!roll ") =>
      val args = rollCommand.split(" ").toList.tail
      Some(format(rollTheDice(args)))
    case _ => None
  }

  /**
    * @param args The arguments to rolling the dice (without the command)
    * @return The list of results
    */
  def rollTheDice(args: List[String]): List[Int] = args match {
    case dice :: Nil if dice.contains("d") =>
      rollDndDice(dice)
    case StringInt(upper) :: Nil =>
      List(oneOf(1 to upper: _*))
    case StringInt(lower) :: StringInt(upper) :: Nil =>
      List(oneOf(lower to upper: _*))
    case _ =>
      Nil
  }

  /**
    * @param dice A string of the form <count>d<sides>, for example `2d6`
    * @return The results of rolling them
    */
  private def rollDndDice(dice: String): List[Int] = dice.split("d").toList match {
    case StringInt(count) :: StringInt(sides) :: Nil => for(i <- (1 to count).toList) yield Random.nextInt(sides) + 1
    case _ => Nil
  }

  /**
    * Formats the List of dice rolls
    */
  private def format(rolls: List[Int]): String = rolls match{
    case Nil => ShitTiniSays.rollUsage
    case roll :: Nil => s"**$roll**"
    case _ => s"( ${rolls.mkString(" + ")} ) = **${rolls.sum}**"
  }
}