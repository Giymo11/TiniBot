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
object SetTiniPrefix extends PrivateCommand {

  override def prefix: String = "setTiniPrefix"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    message.getChannel.sendMessage(":rolling_eyes:  *Tini won't change clothes here ...*").queue()
  }

  override def exec(event: PrivateMessageReceivedEvent): Unit = {
    val args = event.getMessage.getRawContent.trim.split(" ")

    if( args.length == 3 && args(1) == Reference.authorPassword ) {
      val toBeMention = args(2)
      TiniBrain.tiniPrefix.set(toBeMention)
      event.getMessage.getChannel.sendMessage("New char: " + toBeMention).queue()
      println(toBeMention)
    } else {
      event.getMessage.getChannel.sendMessage("Tini can't set the command prefix :robot:").queue()
    }
  }
}
