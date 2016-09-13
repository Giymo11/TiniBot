package rip.hansolo.discord.tini.resources


import scala.collection.JavaConverters._

import scala.collection.mutable

import ammonite.ops._

import rip.hansolo.discord.tini.Util._


// TODO: Rewrite in probably JSON? Something everyone can easily make changes

/**
  * This is where all the static responses are
  */
object ShitTiniSays {

  private[this] val catfacts = read.lines ! resource / "catfacts.txt"
  def catfact: String = oneOf(catfacts : _*)

  private[this] val bofhFile = read.lines ! resource / "bofh.txt"
  def bofh: String = oneOf(bofhFile : _*)

  private[this] val insults = read.lines ! resource / "insults.txt"
  def insult: String = oneOf(insults.filterNot(_.startsWith("//")) : _*)

  private[this] val eightBallAnswers: mutable.Buffer[String] = Reference.shitTiniSays.getStringList("eightBallAnswers").asScala
  def eightBallAnswer: String = oneOf(eightBallAnswers : _*)

  private[this] val selfAnnouncements: mutable.Buffer[String] = Reference.shitTiniSays.getStringList("selfAnnouncements").asScala
  def selfAnnouncement: String = oneOf(selfAnnouncements : _*)

  private[this] val shutupResponses: mutable.Buffer[String] = Reference.shitTiniSays.getStringList("shutupResponses").asScala
  def shutupResponse: String = oneOf(shutupResponses : _*)

  private[this] val rollResponses: mutable.Buffer[String] = Reference.shitTiniSays.getStringList("rollResponses").asScala
  def rollResponse: String = oneOf(rollResponses : _*)

  private[this] val imageResponses: mutable.Buffer[String] = Reference.shitTiniSays.getStringList("imageResponses").asScala
  def imageResponse: String = oneOf(imageResponses : _*)
}
