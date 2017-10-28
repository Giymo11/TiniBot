package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import rip.hansolo.discord.tini.brain.TiniBrain
import rip.hansolo.discord.tini.resources.Reference

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object Kill extends PrivateCommand {
  override def prefix: String = "kill"

  override def exec(event: PrivateMessageReceivedEvent): Unit = {
    val client = event.getJDA
    val content = event.getMessage.getContent.trim

    if( content.contains(Reference.authorPassword) ) {
      client.shutdownNow()
      TiniBrain.killYourself()
    } else {
      event.getMessage.getChannel.sendMessage("Not today :robot:").queue()
    }
  }

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    message.getChannel.sendMessage(" *Too many People are watching, you can't kill Tini here* ").queue()
  }
}
