package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.core.entities.{Game, Message}
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import rip.hansolo.discord.tini.resources.Reference

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object BotStatus extends PrivateCommand {
  override def exec(event: PrivateMessageReceivedEvent): Unit = {
    val client = event.getJDA
    val content = event.getMessage.getContent.trim

    if( content.contains(Reference.authorPassword) ) {
      val status = content.replace("!botstatus", "").replace(Reference.authorPassword, "").trim
      client.getPresence.setGame(Game.of(status))
      event.getMessage.getChannel.sendMessage("status set").queue()
    } else {
      event.getMessage.getChannel.sendMessage("bli bla blub error ... :robot:").queue()
    }
  }

  override def prefix: String = "botstatus"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    message.getChannel.sendMessage(" *Tini listens to loud music and can't hear you screaming* :musical_note: ").queue()
  }
}
