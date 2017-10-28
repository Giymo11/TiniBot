package rip.hansolo.discord.tini.commands

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.core.entities.{Message, MessageChannel}
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.mal.api.MyAnimeListAPI
import rip.hansolo.discord.tini.mal.model._
import rip.hansolo.discord.tini.resources._


/**
  * User: Michael Reitgruber
  * Date: 18.08.2016
  * Time: 12:41
  */
object Animelist extends Command {
  override def prefix = "mal"

  lazy val api = new MyAnimeListAPI(Reference.malUser, Reference.malPass)

  def sendUsage(channel: MessageChannel): Unit = {
    channel.sendMessage(longHelp).queue()
  }

  def sendResponse(manga: Manga, channel: MessageChannel): Unit = {
    val link = "http://myanimelist.net/manga/"+manga.id
    val response =
      s"""
         .$link
         .Score: ${manga.score} | Volumes: ${manga.volumes} | Chapters: ${manga.chapters}
       """.stripMargin('.')
    channel.sendMessage(response).queue()
  }

  def sendResponse(anime: Anime, channel: MessageChannel): Unit = {
    val link = "http://myanimelist.net/anime/"+anime.id
    val response =
      s"""
         .$link
         .Score: ${anime.score} | Episodes: ${anime.episodes} | Type: ${anime.showType}
       """.stripMargin('.')
    channel.sendMessage(response).queue()
  }
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    val arguments = args.split(" ")
    if(arguments.isEmpty)
      sendUsage(message.getChannel)
    else {
      Task[Unit] {
        arguments(0) match {
          case s if s equals "anime" =>
            api.findAnime(arguments.tail.mkString(" ")) match {
              case Some(x :: xs) => sendResponse(x, message.getChannel)
              case _ => message.getChannel.sendMessage("Anime not found").queue()
            }
          case s if s equals "manga" =>
            api.findManga(arguments.tail.mkString(" ")) match {
              case Some(x :: xs) => sendResponse(x, message.getChannel)
              case _ => message.getChannel.sendMessage("Manga not found").queue()
            }
          case _ =>
            api.findAnime(arguments.mkString(" ")) match {
              case Some(x :: xs) => sendResponse(x, message.getChannel)
              case _ => message.getChannel.sendMessage("Anime not found").queue()
            }
        }
      }.runAsync
    }
  }
}
