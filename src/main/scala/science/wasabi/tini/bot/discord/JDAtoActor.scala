package science.wasabi.tini.bot.discord

import akka.actor.ActorRef
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import science.wasabi.tini.bot.discord.ShardActor._

/**
  * Created by Giymo11 on 4/7/2017.
  */
class JDAtoActor(val actor: ActorRef) extends ListenerAdapter {

  val shouldIgnoreSelf = true

  override def onReady(event: ReadyEvent): Unit = {
    actor ! JdaReady
  }

  override def onGuildMessageReceived(event: GuildMessageReceivedEvent): Unit = {
    val authorId = event.getAuthor.getId
    val selfId = event.getJDA.getSelfUser.getId
    val isSelf = authorId equals selfId

    if(!isSelf) {
      val authorMention = event.getAuthor.getAsMention
      val text = event.getMessage.getRawContent
      val channelId = event.getChannel.getId
      actor ! Message(authorMention, text, channelId)
    }
  }
}
