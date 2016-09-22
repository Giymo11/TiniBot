package rip.hansolo.discord.tini.commands

import cats.data.Xor
import monix.eval.Task
import net.dv8tion.jda.entities.{Message, VoiceChannel}
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.audio.player.{BasicPlayer, RadioPlayer, YoutubePlayer}

import scala.collection.concurrent.TrieMap
import monix.execution.Scheduler.Implicits.global
import rip.hansolo.discord.tini.audio.util.WebRadioFfmpegStream

/**
  * Created by: 
  *
  * @author Raphael
  * @version 29.08.2016
  */
object Radio extends Command {
  override def prefix: String = "radio"

  private val onlinePlayers: TrieMap[String,BasicPlayer] = new TrieMap[String,BasicPlayer]()

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    val arguments = args.split(" ")
    if( arguments.length == 1 ) {
      val resource  = arguments(0)
      if( resource == "exit" ) {
        message.getChannel.sendMessageAsync("Ok, Tini will leave the VoiceChannel",null)
        event.getGuild.getAudioManager.closeAudioConnection()
        return
      }
      val userVoice = event.getGuild.getVoiceStatusOfUser(event.getAuthor)

      userVoice.getChannel match {
        case null => message.getChannel.sendMessageAsync("Join a Voice Channel so i can speak with you",null)
        case _ if event.getGuild.getAudioManager.getConnectedChannel == userVoice =>
          val player = onlinePlayers(userVoice.getChannel.getId)
          this.playResource(resource, player, message, event, userVoice.getChannel)

        case _ if event.getGuild.getAudioManager.getConnectedChannel != userVoice =>
          val player = onlinePlayers.get(userVoice.getChannel.getId)

          player match {
            case Some(p) if p.isStopped => playResource(resource, p, message, event, userVoice.getChannel)
            case Some(p) if p.isPlaying => p.stop() //message.getChannel.sendMessageAsync("*Tini is allready speaking ...*",null)
            case _ =>
              val newPlayer = new RadioPlayer(g = event.getGuild)
              onlinePlayers.put(userVoice.getChannel.getId,newPlayer)

              playResource(resource, newPlayer, message, event, userVoice.getChannel)
          }
      }

    } else {
      message.getChannel.sendMessageAsync(longHelp,null)
    }
  }

  /* will join the first VoiceChannel with that name! */
  private def getVoiceChannel(msg: Message,name: String): Option[VoiceChannel] = {
    Xor.catchNonFatal( msg.getJDA.getVoiceChannelByName(name).get(0) ).toOption
  }
  private def playResource(resource: String,player: BasicPlayer,message: Message,event: GuildMessageReceivedEvent,userVoice: VoiceChannel): Unit = {
    Task {
      event.getGuild.getAudioManager.closeAudioConnection()

      player.load(resource)
      event.getGuild.getAudioManager.openAudioConnection( userVoice )
      player.play()

    }.runAsync

    message.getChannel.sendMessageAsync("There you go ",null)
  }

}
