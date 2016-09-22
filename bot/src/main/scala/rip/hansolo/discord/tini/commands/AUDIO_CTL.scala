package rip.hansolo.discord.tini.commands

import net.dv8tion.jda.entities.Message
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.audio.util.FFMPEG

/**
  * Created by: 
  *
  * @author Raphael
  * @version 22.08.2016
  */
object AUDIO_CTL extends Command {

  override def prefix: String = "radio-offset"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    FFMPEG.offset.set( args.toInt )
    FFMPEG.offset.synchronized {
      FFMPEG.offset.notifyAll()
    }


    println("Offset was set and buffer should be empty! ")
  }
}
