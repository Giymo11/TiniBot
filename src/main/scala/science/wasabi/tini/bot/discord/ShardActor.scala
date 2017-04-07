package science.wasabi.tini.bot.discord

import akka.actor._
import akka.event.Logging

import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.core._

import science.wasabi.tini.bot.discord.ShardActor._



object ShardActor {
  case class StartShard(token: String, shardNumber: Int, shardTotal: Int)
  case object JdaReady
  case class Message(author: String, text: String, channelId: String)
}

/**
  * Created by Giymo11 on 4/7/2017.
  */
class ShardActor extends Actor {

  val log = Logging(context.system, this)

  var jda: JDA = _

  override def receive: Receive = {
    case StartShard(token, shardNumber, shardTotal) =>
      log.info("starting shard " + shardNumber)
      jda = new JDABuilder(AccountType.BOT)
        .setToken(token)
        .useSharding(shardNumber, shardTotal)
        .addListener(new JDAtoActor(self))
        .buildAsync()

    case JdaReady =>
      log.info("jda is ready")
      context.become(ready)
      jda.getGuilds.forEach(guild => log.info("connected to " + guild.getName))

    case x =>
      log.warning("Not ready yet! " + x)
  }

  def ready: Receive = {
    case Message(author, text, channelId) =>
      log.debug("responding to " + text)
      val channel = jda.getTextChannelById(channelId)
      log.debug("in channel " + channel.getName)
      channel.sendMessage("Yo " + author + ", I like your " + text).queue()

    case x =>
      log.warning("didnt understand anything " + x)
  }
}





