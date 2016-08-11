package rip.hansolo.discord.tini.brain

import java.time.temporal.ChronoUnit
import java.util.function.Predicate
import scala.collection.JavaConverters._

import net.dv8tion.jda.entities._
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.hooks.ListenerAdapter
import rip.hansolo.discord.tini.resources._

/**
  * The brain region for responding to text messages
  */
class TextBrainRegion extends ListenerAdapter {

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
    content match {
      case command if content.contains("!kill") && content.contains(Resources.authorPassword) =>
        client.shutdown(true)
        TiniBrain.killYourself()
      case _ =>
        ()
    }
  }

  /**
    *
    * @param channel The TextChannel the conversation takes place. Because of this, we are in Guild territory
    */
  private[this] def handleMessage(message: Message, channel: TextChannel) = {

    // TODO: Use a logger
    val timer = (myMessage: Message) => println("Sent response at " + myMessage.getTime + ", after " + ChronoUnit.MILLIS.between(myMessage.getTime, message.getTime))

    import rip.hansolo.discord.tini.commands._
    import rip.hansolo.discord.tini.Util._

    val command = message.getContent.trim

    val isSelf = (user: User) => user.getId == message.getJDA.getSelfInfo.getId

    val otherMentions = message.getMentionedUsers.asScala.filterNot(isSelf)

    // TODO: make more dynamic (as in, allow for later defined commands)
    command match {
      case Bio(args) => args match {
        case Bio.Set(arg) =>
          Bio.Set.setBio(arg, message.getAuthor, message.getChannel)
        case Bio.Get(_) if otherMentions.size == 1 =>
          Bio.Get.tellBio(otherMentions.head, message.getChannel)
        case _ => channel.sendMessageAsync(ShitTiniSays.bioUsage, null)
      }
      case Roll(result) =>
        channel.sendMessageAsync(result, timer)
      case "!help" =>
        channel.sendMessageAsync(ShitTiniSays.help, timer)
      case "!shutup" =>
        TiniBrain.is8ball.set(false)
        channel.sendMessageAsync(ShitTiniSays.shutupResponse, timer)
      case "!8ballmode" =>
        TiniBrain.is8ball.set(true)
        channel.sendMessageAsync(ShitTiniSays.agreement, timer)
      case "!catfacts" =>
        channel.sendMessageAsync(ShitTiniSays.catfact, timer)
      case "!catfacts credits" =>
        channel.sendMessageAsync(ShitTiniSays.credits, timer)
      case _ if TiniBrain.is8ball.get =>
        channel.sendMessageAsync(ShitTiniSays.agreement, timer)
      case _ => ()
    }
  }
}
