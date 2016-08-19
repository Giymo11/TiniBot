package rip.hansolo.discord.tini.commands

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.entities.{Message, MessageChannel}
import rip.hansolo.discord.tini.mal.api.MyAnimeListAPI
import rip.hansolo.discord.tini.mal.model.Anime
import rip.hansolo.discord.tini.resources.{Reference, ShitTiniSays}


/**
  * User: Michael Reitgruber
  * Date: 18.08.2016
  * Time: 12:41
  */
object Animelist extends Command{
  override def prefix = "!mal"

  lazy val api = new MyAnimeListAPI(Reference.malUser, Reference.malPass)

  def sendUsage(channel: MessageChannel): Unit = {
    channel.sendMessageAsync(ShitTiniSays.animelistUsage, null)
  }

  def sendResponse(anime: Anime, channel: MessageChannel): Unit = {
    val link = "http://myanimelist.net/anime/"+anime.id

    val response =
      s"""
         .$link
         .Score: ${anime.score} | Episodes: ${anime.episodes} | Type: ${anime.showType}
       """.stripMargin('.')
    channel.sendMessageAsync(response, null)
  }
  override def exec(args: String, message: Message): Unit = {
    args.length match {
      case 0 => sendUsage(message.getChannel)
      case _ =>
        Task[Unit] {
          val result_list = api.findAnime(args)
          result_list match {
            case Some(x :: xs) => sendResponse(x, message.getChannel)
            case _ => message.getChannel.sendMessageAsync("Anime not found", null)
          }

        }.runAsync
    }
  }
}
