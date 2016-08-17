package rip.hansolo.discord.tini.brain


import java.time.temporal.ChronoUnit

import better.files._
import net.dv8tion.jda.MessageBuilder
import net.dv8tion.jda.entities._
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.hooks.ListenerAdapter
import rip.hansolo.discord.tini.Util
import rip.hansolo.discord.tini.Util._
import rip.hansolo.discord.tini.commands._
import rip.hansolo.discord.tini.resources._

import scala.collection.concurrent.TrieMap


/**
  * The brain region for responding to text messages
  */
object TextBrainRegion extends ListenerAdapter {

  val channelCommands: TrieMap[String,Command] = new TrieMap[String,Command]
  val privateCommands: TrieMap[String,PrivateCommand] = new TrieMap[String,PrivateCommand]

  override def onGuildMessageReceived(event: GuildMessageReceivedEvent): Unit = {
    val channel = event.getChannel
    val message = event.getMessage

    // TODO: Use a logger
    println(s"${channel.getGuild.getName} #${channel.getName} - ${message.getTime} - @${message.getAuthor.getUsername}: ${message.getContent}")

    if(message.getAuthor.getId != event.getJDA.getSelfInfo.getId)
      handleMessage(message, channel)
    else
      println("sent myself")
  }

  override def onPrivateMessageReceived(event: PrivateMessageReceivedEvent): Unit = {
    val content = event.getMessage.getContent.trim

    if( content.startsWith(TiniBrain.prefixChar.get) ) {
     privateCommands.getOrElse(content.split(" ").head.drop(1),NotACommand).exec(event)
    }
  }


  private[this] def logMessage(message: Message): Unit = {
    import better.files._
    (Resources.logPath / (message.getAuthor.getId + ".log")).createIfNotExists() << message.getRawContent
  }

  /**
    *
    * @param channel The TextChannel the conversation takes place. Because of this, we are in Guild territory
    */
  def handleMessage(message: Message, channel: TextChannel) = {

    // TODO: Use a logger
    val timer = (myMessage: Message) => println("Sent response at " + myMessage.getTime + ", after " + ChronoUnit.MILLIS.between(myMessage.getTime, message.getTime))

    val msgText = message.getContent.trim
    if( msgText.startsWith(TiniBrain.prefixChar.get) ) {

      val cmdArgs = msgText.split(" ")
      channelCommands.getOrElse(cmdArgs.head.drop(1),NotACommand).exec(cmdArgs.drop(1).mkString(" "),message)

    } else {
      if(TiniBrain.is8ball.get) {
        val response = new MessageBuilder().appendString(ShitTiniSays.agreement).setTTS(true).build()
        channel.sendMessageAsync(response, timer)
      }

      logMessage(message)
    }
  }
}
