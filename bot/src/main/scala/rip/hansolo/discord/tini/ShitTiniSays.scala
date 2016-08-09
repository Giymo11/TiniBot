package rip.hansolo.discord.tini

import cats.data.Xor

import scala.util.Random

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

  private[this] def rollMessage = Util.oneOf("You rolled: ", ":game_die: says: ", "RNGesus says: ", "My Quantum Random Number Generator says: ", "")
  def rollTheDice(command: String): String = {
    val args = command.trim.split(" ")
    val message = Xor.catchNonFatal {
      if(args.size == 2) {  // !roll 2d6
        val dice = args(1)
        if(dice.contains("d")) {
          val parts = dice.split("d")
          val results =
            if(parts.size == 2) { // 2, 6
              val count = parts(0).toInt
              val sides = parts(1).toInt
              if(count <= 0 || sides <= 0) throw new IllegalArgumentException("I need ma counts")
              for(i <- 1 to count) yield Random.nextInt(sides) + 1
            } else Seq()
          if(results.size == 1) results.head.toString
          else s"( ${results.mkString(" + ")} ) = **${results.sum}**"
        } else {
          val upper = args(1).toInt
          s"**${Util.oneOf(1 to upper: _*)}**"
        }
      } else if (args.size == 3) { // !roll 2 20
      val args = command.trim.split(" ")
        val lower = args(1).toInt
        val upper = args(2).toInt
        s"**${Util.oneOf(lower to upper: _*)}**"
      } else throw new IllegalArgumentException("Needs better arguments!")
    }

    message match {
      case Xor.Right(value) => rollMessage + value
      case Xor.Left(_) =>
        """
          |Usage:
          |`!roll <lower-bound> <upper-bound>`, example: `!roll 1 10`
          |`!roll <count>d<sides>`, example: `!roll 2d6`
        """.stripMargin
    }
  }
}
