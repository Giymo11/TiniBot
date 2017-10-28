package rip.hansolo.discord.tini.audio.util

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.core.entities.{Message, VoiceChannel}
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.audio.player.{BasicPlayer, FFmpegPlayer}

import scala.collection.concurrent.TrieMap
import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by: 
  *
  * @author Raphael
  * @version 28.09.2016
  */
object AudioManager {

  private val onlinePlayers: TrieMap[String,BasicPlayer] = new TrieMap[String,BasicPlayer]()

  def requestPlay(resource: String, event: GuildMessageReceivedEvent, useProxy: Boolean = true): Promise[Boolean] = {
    val userVoice = event.getMember.getVoiceState
    val message   = event.getMessage
    val requestPromise = Promise[Boolean]

    userVoice.getChannel match {
      /* user not in any voice channel */
      case null =>
        message.getChannel.sendMessage("Join a Voice Channel so i can speak with you").queue()
        requestPromise.success( false )


      /* Tini not connected to any audio channel -> happy to play you something */
      case _ if event.getGuild.getAudioManager.getConnectedChannel == null =>
        playResource(resource, new FFmpegPlayer(event.getGuild,useProxy), message, event, userVoice.getChannel,requestPromise)


      /* user in same voice channel as Tini and a player is playing */
      case _ if event.getGuild.getAudioManager.getConnectedChannel == userVoice &&
                onlinePlayers.get(userVoice.getChannel.getId).isDefined =>

        if( isUserAllowedToStopPlayer(userVoice.getMember.getUser.getId,onlinePlayers(userVoice.getChannel.getId)) ) {
          onlinePlayers(userVoice.getChannel.getId).stop()
          playResource(resource, onlinePlayers(userVoice.getChannel.getId), message, event, userVoice.getChannel,requestPromise)
        } else {
          message.getChannel.sendMessage("You are not allowed to stop the current stream!").queue()
          requestPromise.success( false )
        }


      /* user in the same voice channel but player is not playing*/
      case _ if event.getGuild.getAudioManager.getConnectedChannel == userVoice &&
                onlinePlayers.get(userVoice.getChannel.getId).isEmpty =>
        playResource(resource, onlinePlayers(userVoice.getChannel.getId), message, event, userVoice.getChannel,requestPromise)


      /* user in different voice channel as Tini and the player has stopped */
      case _ if event.getGuild.getAudioManager.getConnectedChannel != userVoice &&
                onlinePlayers.get(event.getGuild.getAudioManager.getConnectedChannel.getId).isEmpty =>
            playResource(resource, new FFmpegPlayer(event.getGuild,useProxy), message, event, userVoice.getChannel,requestPromise)


      /* user in different voice channel as Tini and the player has stopped */
      case _ if event.getGuild.getAudioManager.getConnectedChannel != userVoice &&
                onlinePlayers.get(event.getGuild.getAudioManager.getConnectedChannel.getId).isDefined =>

        onlinePlayers.get(userVoice.getChannel.getId) match {
          case Some(p) if p.isStopped => playResource(resource, p, message, event, userVoice.getChannel,requestPromise)
          case Some(p) =>
            p.stop()
            if (isUserAllowedToStopPlayer(userVoice.getMember.getUser.getId, onlinePlayers(userVoice.getChannel.getId)))
              playResource(resource, p, message, event, userVoice.getChannel,requestPromise)
            else {
              message.getChannel.sendMessage("*Tini won't stop listen to you*").queue()
              requestPromise.success( false )
            }

          case _ => requestPromise.success( false ) /* ups that should not happen ... */
        }
    }

    requestPromise
  }

  def requestStop(event: GuildMessageReceivedEvent): Unit = {
    val userVoice = event.getMember.getVoiceState.getChannel
    val message   = event.getMessage

    if( onlinePlayers.get(userVoice.getId).isDefined ) {
      if( isUserAllowedToStopPlayer(event.getAuthor.getId,onlinePlayers(userVoice.getId) ) &&
          event.getGuild.getAudioManager.getConnectedChannel.getId == userVoice.getId ) {
        onlinePlayers(userVoice.getId).stop()
      } else {
        if( event.getGuild.getAudioManager.getConnectedChannel.getId != userVoice.getId  )
          message.getChannel.sendMessage("Please join the voice channel").queue()
        else
          message.getChannel.sendMessage("You don't have the permission to do that").queue()
      }
    } else {
      message.getChannel.sendMessage("Can't remove stream cuz it's allready removed!").queue()
    }

  }


  private def playResource(resource: String, player: BasicPlayer, message: Message, event: GuildMessageReceivedEvent, userVoice: VoiceChannel, requestPromise: Promise[Boolean]): Unit = {
    onlinePlayers.put(userVoice.getId,player)

    Task {
      event.getGuild.getAudioManager.closeAudioConnection()

      message.getChannel.sendMessage("Loading Audio Data ... (Buffering please wait a while)").queue()
      Task.fromFuture( player.load(resource).future )
          .runAsync
          .andThen {
            case Success(u) =>
              requestPromise.success( true )

              /* register handler for removing everything */
              if( player.isInstanceOf[FFmpegPlayer] ) {
                Task.fromFuture( player.asInstanceOf[FFmpegPlayer].media.dead.future ).doOnFinish( x => Task {
                  onlinePlayers.remove( event.getGuild.getId )

                  println("KILL PLAYER HANDLER")

                  player.stop() // should be stopped but who knows
                  player.destroy()
                  event.getGuild.getAudioManager.closeAudioConnection()
                })
              }

            case Failure(ext) => /* shit player shit itself */
              message.getChannel.sendMessage("There was a problem with this stream, Tini couldn't handle it :/").queue()
              player.stop()
              player.destroy()
              event.getGuild.getAudioManager.closeAudioConnection()

              println("ERROR: Exception in player!")
              if( onlinePlayers.get( event.getGuild.getId ).isDefined )
                onlinePlayers.remove( event.getGuild.getId )

              requestPromise.success( false )
          }

      event.getGuild.getAudioManager.openAudioConnection( userVoice )
      player.play()
    }.runAsync

    Task {
      message.getChannel.sendMessage("Here you go, have fun with it").queue()
    }.delayExecution(4.5 seconds)
     .runAsync

  }

  private def isUserAllowedToStopPlayer(user: String,player: BasicPlayer): Boolean = true

}
