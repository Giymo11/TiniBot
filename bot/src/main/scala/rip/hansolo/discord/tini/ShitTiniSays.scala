package rip.hansolo.discord.tini

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
             """.stripMargin

  private[this] val catfacts = {
    import ammonite.ops._
    read.lines ! resource / "catfacts.txt"
  }
  def catfact = Util.oneOf(catfacts :_*)

  def shutupResponse = Util.oneOf(":unamused:", "Rude..", "But don't come crying afterwards!", ":middle_finger:")

}
