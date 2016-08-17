package rip.hansolo.discord.tini.commands


import scala.util.Random

import better.files._

import malakov.Markov

import net.dv8tion.jda.entities._

import rip.hansolo.discord.tini.resources._


/**
  * User: Michael Reitgruber
  * Date: 16.08.2016
  * Time: 14:16
  */
object Imitate extends Command {

  override def prefix:String = "!be"

  override def exec(args: String, message: Message):Unit = {
    val mentions = {
      import scala.collection.JavaConverters._
      message.getMentionedUsers.asScala.toList
    }

    val response = mentions match {
        case user :: Nil =>
          val id = user.getId
          val strings = (Resources.logPath / (id + ".log")).contentAsString.split(" ")
          val len = strings.length
          import scalaz.stream.Process
          Markov.run(1, Process.emitAll(strings.toStream), Random.nextInt(len))
            .takeThrough(line => !line.contains("\n"))
            .map(line => line.takeWhile(_ != '\n'))
            .runLog.unsafePerformSync.mkString(" ")
        case _ =>
          ShitTiniSays.imitateUsage
      }
    message.getChannel.sendMessageAsync(response, null)
  }

  override def execHelp(args: String, message: Message): Unit = {
    message.getChannel.sendMessageAsync("`!be <@user>` - Tini tries to impersonate the other user",null)
  }

  override def getHelp: String =  prefix + " - make the bot impersonate someone"
}
