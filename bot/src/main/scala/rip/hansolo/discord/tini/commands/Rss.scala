package rip.hansolo.discord.tini.commands
import java.net.URL

import cats.data.Xor
import com.rometools.rome.feed.synd._
import com.rometools.rome.io.{SyndFeedInput, XmlReader}
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.brain.TiniBrain

/**
  * Created by Giymo11 on 8/27/2016 at 2:29 AM.
  */
object Rss extends Command{
  override def prefix: String = "rss"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    // TODO: add possibility to restrict items and enable embedding of links
    Xor.catchNonFatal {
      val feedUrl: URL = new URL(args)

      val input: SyndFeedInput = new SyndFeedInput()
      val feed: SyndFeed = input.build(new XmlReader(feedUrl))

      import scala.collection.JavaConverters._

      println("getting stuff for " + args)

      val title = feed.getTitle
      val entries = feed.getEntries.asScala.take(TiniBrain.numberOfRssEntries.get)

      def getFirstLine(str: String) = str.dropWhile(_.isWhitespace).split("\n").head

      // the '<' and '>' around the link disable embedding.
      val entryStrings = for(entry <- entries) yield {
        val link = if(TiniBrain.embedRssLinks.get) entry.getLink else s"<${entry.getLink}>"
        getFirstLine(entry.getTitle) + "\n" + link
      }
      val text = title + "\n" + feed.getLink + "\n\n" + entryStrings.mkString("\n\n")
      message.getChannel.sendMessage(text.take(2000)).queue()

    }.leftMap(_.printStackTrace())
  }
}
