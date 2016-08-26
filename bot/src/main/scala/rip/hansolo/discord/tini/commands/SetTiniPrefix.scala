package rip.hansolo.discord.tini.commands


import net.dv8tion.jda.entities.Message

import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent

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
    message.getChannel.sendMessageAsync(":rolling_eyes:  *Tini won't change clothes here ...*", null)
  }

  override def exec(event: PrivateMessageReceivedEvent): Unit = {
    val args = event.getMessage.getRawContent.trim.split(" ")

    if( args.length == 3 && args(1) == Reference.authorPassword ) {
      val toBeMention = args(2)
      TiniBrain.tiniPrefix.set(toBeMention)
      event.getMessage.getChannel.sendMessageAsync("New char: " + toBeMention, null)
      println(toBeMention)
    } else {
      event.getMessage.getChannel.sendMessageAsync("Tini can't set the command prefix :robot:", null)
    }
  }
}
