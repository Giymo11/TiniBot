package rip.hansolo.discord.tini.commands

import java.net.URL

import cats.data.Xor
import net.dv8tion.jda.JDA
import net.dv8tion.jda.audio.player.{Player, URLPlayer}
import net.dv8tion.jda.entities.{Message, VoiceChannel}
import rip.hansolo.discord.tini.audio.player.MP4UrlPlayer
import rip.hansolo.discord.tini.audio.util.YoutubeUtil

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by: 
  *
  * @author Raphael
  * @version 26.08.2016
  */
object Audio extends Command {
  override def prefix: String = "sound"

  private var onlineChannels: List[VoiceChannel] = List.empty[VoiceChannel]

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {

    println("Channel-Name: " + args.split(" ")(0))
    println("Youtube Link: " + args.split(" ")(1))

    val channel = getVoiceChannel(message,args.split(" ")(0))
    if( channel.isDefined ) {
      if (!onlineChannels.contains(channel.get)) {
        onlineChannels = onlineChannels :+ channel.get
        channel.get.getGuild.getAudioManager.openAudioConnection(channel.get)

        val uri: String = YoutubeUtil.getDownloadURL(args.split(" ")(1)).getOrElse(args.split(" ")(1))
        val player = new MP4UrlPlayer(channel.get.getJDA,new URL(uri))
        player.play()

        channel.get.getGuild.getAudioManager.setSendingHandler(player)

        //player.play()
        message.getChannel.sendMessageAsync("played message!",null)

      } else {
        println("Allready in Channel!")
        channel.get.getGuild.getAudioManager.closeAudioConnection()
        onlineChannels = List.empty
      }
    } else {
      println("Channel not found!")
    }

  }

  /* will join the first VoiceChannel with that name! */
  private def getVoiceChannel(msg: Message,name: String): Option[VoiceChannel] = {
    Xor.catchNonFatal( msg.getJDA.getVoiceChannelByName(name).get(0) ).toOption
  }

  override def longHelp: String = shortHelp
  override def shortHelp: String = s"`$command <channel> <url>` - plays the mp4 file from the url in the channel "
}
