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
class TextBrainRegion extends ListenerAdapter {

  val commands: TrieMap[String,Command] = new TrieMap[String,Command]

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
    val client = event.getJDA
    val content = event.getMessage.getContent.trim

    println(content)
    content match {
      case command if command.contains("!kill") && command.contains(Resources.authorPassword) =>
        client.shutdown(true)
        TiniBrain.killYourself()
      case command if command.contains("!botstatus") && command.contains(Resources.authorPassword) =>
        val status = command.replace("!botstatus", "").replace(Resources.authorPassword, "").trim
        client.getAccountManager.setGame(status)
        event.getMessage.getChannel.sendMessageAsync("status set", null)
      case _ =>
        ()
    }
  }


  private[this] def logMessage(message: Message): Unit = {
    (Resources.logPath/s"${message.getAuthor.getId}.log").createIfNotExists() << message.getRawContent
  }
  /**
    *
    * @param channel The TextChannel the conversation takes place. Because of this, we are in Guild territory
    */
  def handleMessage(message: Message, channel: TextChannel) = {

    // TODO: Use a logger
    val timer = (myMessage: Message) => println("Sent response at " + myMessage.getTime + ", after " + ChronoUnit.MILLIS.between(myMessage.getTime, message.getTime))

    val msgText = message.getContent.trim
    if( msgText.startsWith("!") ) {
      val cmdArgs = msgText.split(" ")
      val command = commands.get(cmdArgs(0))

      println(cmdArgs(0))
      println(commands)

      if( command.isEmpty ) message.getChannel.sendMessageAsync(s"Command ${cmdArgs(0)} is not known!",null)
      else command.get.exec(cmdArgs.drop(1).mkString(" "),message)


    } else {
      if(TiniBrain.is8ball.get) {
        val response = new MessageBuilder().appendString(ShitTiniSays.agreement).setTTS(true).build()
        channel.sendMessageAsync(response, timer)
      }

      logMessage(message)
    }
  }
}
