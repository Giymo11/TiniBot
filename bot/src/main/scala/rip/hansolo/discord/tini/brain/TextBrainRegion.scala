package rip.hansolo.discord.tini.brain


import java.time.temporal.ChronoUnit

import scala.collection.concurrent.TrieMap

import net.dv8tion.jda.MessageBuilder
import net.dv8tion.jda.entities._
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.hooks.ListenerAdapter

import rip.hansolo.discord.tini.Util._
import rip.hansolo.discord.tini.commands._
import rip.hansolo.discord.tini.resources._


/**
  * The brain region for responding to text messages
  */
object TextBrainRegion extends ListenerAdapter {

  val channelCommands: TrieMap[String, Command] = new TrieMap[String, Command]
  val privateCommands: TrieMap[String, PrivateCommand] = new TrieMap[String, PrivateCommand]

  def charsToDrop(implicit brain: LocalSettings): Int = brain.tiniPrefix.length

  override def onGuildMessageReceived(event: GuildMessageReceivedEvent): Unit = {

    implicit val brain = SettingsBrain.getFor(event.getGuild.getId)

    val channel = event.getChannel
    val message = event.getMessage

    // TODO: Use a logger
    println(s"${channel.getGuild.getName} #${channel.getName} - ${message.getTime} - @${message.getAuthor.getUsername}: ${message.getContent}")

    if(message.getAuthor.getId != event.getJDA.getSelfInfo.getId)
      handleMessage(message, channel)
  }

  override def onPrivateMessageReceived(event: PrivateMessageReceivedEvent): Unit = {

    implicit val brain = SettingsBrain.getForPrivate(event.getAuthor.getId)

    val message = event.getMessage

    val rawContent = message.getRawContent.trim

    rawContent match {
      case x if x.startsWith(brain.tiniPrefix) =>
        val args = rawContent.dropWhile(isWhitespace).drop(charsToDrop).dropWhile(isWhitespace).replace("\n", " ").split(" ")
        println("Prefix: " + args.head)
        privateCommands.getOrElse(args.head, NotACommand).exec(event)
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
  def handleMessage(message: Message, channel: TextChannel)(implicit brain: LocalSettings): Unit = {

    // TODO: Use a logger
    val timer = (myMessage: Message) => println("Sent response at " + myMessage.getTime + ", after " + ChronoUnit.MILLIS.between(myMessage.getTime, message.getTime))

    val rawContent = message.getRawContent.trim

    rawContent match {
      case x if x.startsWith(brain.tiniPrefix)=>
        val args = rawContent.dropWhile(isWhitespace).drop(charsToDrop).dropWhile(isWhitespace).replace("\n", " ").split(" ")
        println("Prefix: " + args.head)
        exec(args.toList, MessageData.from(message))
      case _ if brain.is8ball =>
        val response = new MessageBuilder().appendString(ShitTiniSays.eightBallAnswer).setTTS(true).build()
        channel.sendMessageAsync(response, timer)
      case _ =>
        logMessage(message)
    }
  }

  def exec(args: List[String], message: MessageData)(implicit brain: LocalSettings): Unit = {
    channelCommands.getOrElse(args.head, NotACommand).exec(args.tail.mkString(" "), message)
  }
}
