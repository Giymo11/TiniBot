package rip.hansolo.discord.tini.commands

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.entities.{Message, MessageChannel}
import rip.hansolo.discord.tini.mal.api.MyAnimeListAPI
import rip.hansolo.discord.tini.mal.model.{Anime, Manga}
import rip.hansolo.discord.tini.resources.{Reference, ShitTiniSays}


/**
  * User: Michael Reitgruber
  * Date: 18.08.2016
  * Time: 12:41
  */
object Animelist extends Command {
  override def prefix = "mal"

  lazy val api = new MyAnimeListAPI(Reference.malUser, Reference.malPass)

  def sendUsage(channel: MessageChannel): Unit = {
    channel.sendMessageAsync(ShitTiniSays.animelistUsage, null)
  }

  def sendResponse(manga: Manga, channel: MessageChannel): Unit = {
    val link = "http://myanimelist.net/manga/"+manga.id
    val response =
      s"""
         .$link
         .Score: ${manga.score} | Volumes: ${manga.volumes} | Chapters: ${manga.chapters}
       """.stripMargin('.')
    channel.sendMessageAsync(response, null)
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
    val arguments = args.split(" ")
    if(arguments.isEmpty) sendUsage(message.getChannel)
    else {
      Task[Unit] {
        arguments(0) match {
          case s if s equals "anime" =>
            api.findAnime(arguments.tail.mkString(" ")) match {
              case Some(x :: xs) => sendResponse(x, message.getChannel)
              case _ => message.getChannel.sendMessageAsync("Anime not found", null)
            }
          case s if s equals "manga" =>
            api.findManga(arguments.tail.mkString(" ")) match {
              case Some(x :: xs) => sendResponse(x, message.getChannel)
              case _ => message.getChannel.sendMessageAsync("Manga not found", null)
            }
          case _ =>
            api.findAnime(arguments.mkString(" ")) match {
              case Some(x :: xs) => sendResponse(x, message.getChannel)
              case _ => message.getChannel.sendMessageAsync("Anime not found", null)
            }
        }
      }.runAsync
    }
  }

  override def longHelp: String = s"`$command <anime|manga> <name>` - Gives you information about the anime or manage with the specific name"
  override def shortHelp: String = s"`$command` - Gives you information about anime and manga"
}
