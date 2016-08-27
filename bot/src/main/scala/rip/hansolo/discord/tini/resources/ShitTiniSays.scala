package rip.hansolo.discord.tini.resources


import ammonite.ops._

import rip.hansolo.discord.tini.Util._

import scala.collection.JavaConverters._


// TODO: Rewrite in probably JSON? Something everyone can easily make changes

/**
  * This is where all the static responses are
  */
object ShitTiniSays {

  private[this] val catfacts = read.lines ! resource / "catfacts.txt"
  def catfact = oneOf(catfacts : _*)

  private[this] val bofhFile = read.lines ! resource / "bofh.txt"
  def bofh = oneOf(bofhFile : _*)

  private[this] val insults = read.lines ! resource / "insults.txt"
  def insult = oneOf(insults.filterNot(_.startsWith("//")) : _*)

  val eightBallAnswers = Reference.shitTiniSays.getStringList("eightBallAnswers").asScala
  def eightBallAnswer = oneOf(eightBallAnswers : _*)

  val selfAnnouncements = Reference.shitTiniSays.getStringList("selfAnnouncements").asScala
  def selfAnnouncement = oneOf(selfAnnouncements : _*)

  val shutupResponses = Reference.shitTiniSays.getStringList("shutupResponses").asScala
  def shutupResponse = oneOf(shutupResponses : _*)

  val rollResponses = Reference.shitTiniSays.getStringList("rollResponses").asScala
  def rollResponse = oneOf(rollResponses : _*)

  val imageResponses = Reference.shitTiniSays.getStringList("imageResponses").asScala
  def imageResponse = oneOf(imageResponses : _*)
}
