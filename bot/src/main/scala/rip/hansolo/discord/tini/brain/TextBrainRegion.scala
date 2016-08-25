package rip.hansolo.discord.tini.brain


import java.time.temporal.ChronoUnit

import net.dv8tion.jda.MessageBuilder
import net.dv8tion.jda.entities._
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.hooks.ListenerAdapter
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

  def charsToDrop = TiniBrain.tiniPrefix.get.length

  override def onGuildMessageReceived(event: GuildMessageReceivedEvent): Unit = {
    val channel = event.getChannel
    val message = event.getMessage

    // TODO: Use a logger
    println(s"${channel.getGuild.getName} #${channel.getName} - ${message.getTime} - @${message.getAuthor.getUsername}: ${message.getContent}")

    if(message.getAuthor.getId != event.getJDA.getSelfInfo.getId)
      handleMessage(message, channel)
  }

  override def onPrivateMessageReceived(event: PrivateMessageReceivedEvent): Unit = {
    val message = event.getMessage

    val content = message.getContent.trim
    val rawContent = message.getRawContent.trim

    rawContent match {
      case x if x.startsWith(TiniBrain.tiniPrefix.get) =>
        privateCommands.getOrElse(rawContent.split(" ").head.drop(charsToDrop), NotACommand).exec(event)
      case _ => /* no command just some random message */
    }
  }

  private[this] def logMessage(message: Message): Unit = {
    import better.files._
    (Reference.logPath / (message.getAuthor.getId + ".log")).createIfNotExists() << message.getRawContent
  }

  /**
    *
    * @param channel The TextChannel the conversation takes place. Because of this, we are in Guild territory
    */
  def handleMessage(message: Message, channel: TextChannel) = {

    // TODO: Use a logger
    val timer = (myMessage: Message) => println("Sent response at " + myMessage.getTime + ", after " + ChronoUnit.MILLIS.between(myMessage.getTime, message.getTime))

    val content = message.getContent.trim
    val rawContent = message.getRawContent.trim

    // TODO: enable usage of ' ' and '\n' in between tiniPrefix and normal prefix

    rawContent match {
      case x if x.startsWith(TiniBrain.tiniPrefix.get)=>
        channelCommands.getOrElse(rawContent.split(" ").head.drop(charsToDrop), NotACommand).exec(content.split(" ").drop(1).mkString(" "), message)
      case _ if TiniBrain.is8ball.get =>
        val response = new MessageBuilder().appendString(ShitTiniSays.agreement).setTTS(true).build()
        channel.sendMessageAsync(response, timer)
      case _ =>
        logMessage(message)
    }

  }
}
