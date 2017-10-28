package rip.hansolo.discord.tini.commands

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.audio.player.BasicPlayer
import rip.hansolo.discord.tini.audio.util.{AudioManager, YoutubeUtil}

import scala.collection.concurrent.TrieMap
import scala.util.{Failure, Success, Try}


/**
  * Created by: 
  *
  * @author Raphael
  * @version 26.08.2016
  */
object Youtube extends Command {
  override def prefix: String = "youtube"

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
      if( resource == "stop" ) {
        AudioManager.requestStop(event)
      } else {

        var currentIndex = 0
        var yturls = YoutubeUtil.getDownloadURL(resource)
        if (yturls.isEmpty)
          yturls = YoutubeUtil.getDownloadURL(resource, "video/webm")

        println(yturls)
        tryPlayYoutubeVideo(yturls,0,event)
      }

    } else {
      message.getChannel.sendMessage(longHelp).queue()
    }
  }

 private def tryPlayYoutubeVideo(yturls: List[String], currentIndex: Int, event: GuildMessageReceivedEvent): Unit = {
   Try(println("Try to play: ("+currentIndex+"/"+yturls.length+")" + yturls(currentIndex) ))

   if( currentIndex >= yturls.length ) event.getMessage.getChannel.sendMessage("Tini can not play the video :cry:").queue()
   else
   Task.fromFuture( AudioManager.requestPlay( yturls(currentIndex) , event , useProxy = false ).future )
       .runAsync
       .andThen {
         case Success(status) if !status => tryPlayYoutubeVideo(yturls, currentIndex + 1,event)
         case Success(stats) if stats => println("Seems to work, will play this one") /* it worked nothing to do ... */
         case Failure(ex) => ex.printStackTrace() /* nothing to do, hard crash on this one ... */
     }
 }

}
