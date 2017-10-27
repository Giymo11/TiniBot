package rip.hansolo.discord.tini.commands

import net.dv8tion.jda.entities.Message
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.audio.util.AudioManager

/**
  * Created by: 
  *
  * @author Raphael
  * @version 29.08.2016
  */
object Radio extends Command {
  override def prefix: String = "radio"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    val arguments = args.split(" ")
    if( arguments.length == 1 ) {

      val resource  = arguments(0)
      if( resource == "stop" ) {
        AudioManager.requestStop(event)
      } else {
        AudioManager.requestPlay(resource,event,useProxy = false)
        /* we can't handle anything */
      }

    } else {
      message.getChannel.sendMessageAsync(longHelp,null)
    }
  }

}
