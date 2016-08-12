package rip.hansolo.discord.tini.resources


import ammonite.ops._

import rip.hansolo.discord.tini.Util._


/**
  * This is where all the static responses are
  */
object ShitTiniSays {
  val catUsage =
    """
      |Usage:
      |`!catfacts` to get catfacts
      |`!catfacts credits` to get those
    """.stripMargin


  private[this] val catfacts = read.lines ! resource / "catfacts.txt"
  def catfact = oneOf(catfacts :_*)

  def agreement = oneOf(
    "I disagree :raised_hand:",
    "I agree :ok_hand:"
  )

  def selfAnnouncement = oneOf(
    "Yo! Did you miss me?",
    "What's up?",
    "Wazzap? #hype",
    "Ohayou! :v:",
    "I come in peace! :vulcan:"
  )

  val credits = """
    |Credits:
    |http://facts.randomhistory.com/interesting-facts-about-cats.html
    |https://www.buzzfeed.com/chelseamarshall/meows?utm_term=.dtmN8lOYZ#.pumZyapEG
    |https://www.reddit.com/r/funny/comments/oyokn/it_seems_to_be_catching_on/c3l5v8r
  """.stripMargin

  val help ="""
    |Available Commands:
    |!help - I will display this help message. Again.
    |!catfacts - I will show you some catfacts. :cat2:
    |!shutup - I will not respond to every message anymore.
    |!8ballmode - I will start responding again. Default mode :stuck_out_tongue_winking_eye:
    |!roll <lower-bound> <upper-bound> - Returns a number between those numbers
    |!bio - to set your and display other biographies
  """.stripMargin

  def shutupResponse = oneOf(
    ":unamused:",
    "Rude..",
    "But don't come crying afterwards!",
    ":middle_finger:"
  )

  def rollAnnouncement = oneOf(
    "You rolled: ",
    ":game_die: says: ",
    "RNGesus says: ",
    "My Quantum Random Number Generator says: ",
    ""
  )

  val rollUsage = """
    |Usage:
    |`!roll <lower> <upper>`, example: `!roll 1 10`
    |`!roll <count>d<sides>`, example: `!roll 2d6`
  """.stripMargin

  val bioUsage ="""
    |Usage:
    |`!bio set <text>` to set your biography
    |`!bio <@mention>` to display someones biography
  """.stripMargin
}
