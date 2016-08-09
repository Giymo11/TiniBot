package rip.hansolo.discord.tini

import cats.data.Xor

import scala.util.{Random, Try}

/**
  * This is where all the static responses are
  */
object ShitTiniSays {
  def agreement = Util.oneOf(
    "I disagree :raised_hand:",
    "I agree :ok_hand:"
  )

  def selfAnnouncement = Util.oneOf(
    "Yo! Did you miss me?",
    "What's up?",
    "Wazzap? #hype",
    "Ohayou! :v:",
    "I come in peace! :vulcan:"
  )

  val credits = """Credits:
                  |http://facts.randomhistory.com/interesting-facts-about-cats.html
                  |https://www.buzzfeed.com/chelseamarshall/meows?utm_term=.dtmN8lOYZ#.pumZyapEG
                  |https://www.reddit.com/r/funny/comments/oyokn/it_seems_to_be_catching_on/c3l5v8r
                """.stripMargin

  val help = """Available Commands:
               |!help - I will display this help message. Again.
               |!catfacts - I will show you some catfacts. :cat2:
               |!shutup - I will not respond to every message anymore.
               |!8ballmode - I will start responding again. Default mode :stuck_out_tongue_winking_eye:
               |!roll <lower-bound> <upper-bound> - Returns a number between those numbers
             """.stripMargin

  private[this] val catfacts = {
    import ammonite.ops._
    read.lines ! resource / "catfacts.txt"
  }
  def catfact = Util.oneOf(catfacts :_*)

  def shutupResponse = Util.oneOf(":unamused:", "Rude..", "But don't come crying afterwards!", ":middle_finger:")

  private[this] def rollMessage = Util.oneOf(
    "You rolled: ",
    ":game_die: says: ",
    "RNGesus says: ",
    "My Quantum Random Number Generator says: ",
    ""
  )

  private[this] val rollUsage = """
                                  |Usage:
                                  |`!roll <lower-bound> <upper-bound>`, example: `!roll 1 10`
                                  |`!roll <count>d<sides>`, example: `!roll 2d6`
                                """.stripMargin

  def rollTheDice(command: String): String = {
    val args = command.trim.split(" ").toList

    object ExtractInt {
      def unapply(arg: String): Option[Int] = Xor.catchNonFatal(arg.toInt).toOption
    }

    def getResultsForDice(dice: String): Seq[Int] = dice.split("d").toList match {
      case ExtractInt(count) :: ExtractInt(sides) :: Nil => for(i <- 1 to count) yield Random.nextInt(sides) + 1
      case _ => Seq()
    }

    val result = args match {
      case _ :: dice :: Nil if dice.contains("d") =>
        val results = getResultsForDice(dice)
        Some(s"( ${results.mkString(" + ")} ) = **${results.sum}**")
      case _ :: ExtractInt(upper) :: Nil =>
        Some(s"**${Util.oneOf(1 to upper: _*)}**")
      case _ :: ExtractInt(lower) :: ExtractInt(upper) :: Nil =>
        Some(s"**${Util.oneOf(lower to upper: _*)}**")
      case _ =>
        None
    }

    result match {
      case Some(value) => rollMessage + value
      case None => rollUsage
    }
  }
}
