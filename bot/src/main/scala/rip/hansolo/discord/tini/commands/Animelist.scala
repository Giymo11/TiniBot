package rip.hansolo.discord.tini.commands

import com.mashape.unirest.http.Unirest
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.entities.{Message, MessageChannel}
import rip.hansolo.discord.tini.resources.{Reference, ShitTiniSays}


/**
  * User: Michael Reitgruber
  * Date: 18.08.2016
  * Time: 12:41
  */
object Animelist extends Command{
  override def prefix = "!mal"

  def sendUsage(channel: MessageChannel): Unit = {
    channel.sendMessageAsync(ShitTiniSays.animelistUsage, null)
  }

  def sendResponse(result: scala.xml.Node, channel: MessageChannel): Unit = {
    val score = (result\"score").text
    val typ = (result\"type").text
    val id = (result\"id").text
    val link = "http://myanimelist.net/anime/"+id
    val episodes = (result\"episodes").text

    val response =
      s"""
         .$link
         .Score: $score | Episodes: $episodes | Type: $typ
       """.stripMargin('.')
    channel.sendMessageAsync(response, null)
  }
  override def exec(args: String, message: Message): Unit = {
    args.length match {
      case 0 => sendUsage(message.getChannel)
      case _ =>
        val req = Unirest.get("http://myanimelist.net/api/anime/search.xml")
          .basicAuth(Reference.malUser, Reference.malPass)
          .queryString("q", args)
        Task[Unit] {
          val resp = req.asString
          resp.getStatus match {
            case 204 => message.getChannel.sendMessageAsync("Anime not found", null)
            case _ =>
              val firstResult = scala.xml.XML.loadString(resp.getBody).head.child(1)
              sendResponse(firstResult, message.getChannel)
          }

        }.runAsync
    }
  }
}
