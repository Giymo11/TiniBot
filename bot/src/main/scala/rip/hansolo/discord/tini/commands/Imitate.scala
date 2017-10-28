package rip.hansolo.discord.tini.commands


import better.files._
import malakov.Markov
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.resources.Reference

import scala.util.Random


/**
  * User: Michael Reitgruber
  * Date: 16.08.2016
  * Time: 14:16
  */
object Imitate extends Command {

  override def prefix:String = "be"

  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent):Unit = {
    val mentions = {
      import scala.collection.JavaConverters._
      message.getMentionedUsers.asScala.toList
    }

    val response = mentions match {
        case user :: Nil =>
          val id = user.getId
          val strings = (Reference.logPath / (id + ".log")).contentAsString.split(" ")
          val len = strings.length
          import scalaz.stream.Process
          Markov.run(1, Process.emitAll(strings.toStream), 0)
            .drop(Random.nextInt(len))
            .takeThrough(line => !line.contains("\n"))
            .map(line => line.takeWhile(_ != '\n'))
            .runLog.unsafePerformSync.mkString(" ")
        case _ =>
          longHelp
      }
    message.getChannel.sendMessage(response).queue()
  }
}
