package science.wasabi.tini.bot

import akka.actor.{ActorSystem, PoisonPill, Props}
import science.wasabi.tini.bot.discord.DiscordActor

/**
  * Created by Giymo11 on 4/6/2017.
  */
object Main extends App {
  val system = ActorSystem("BotActorSystem")

  val actor = system.actorOf(Props[DiscordActor], "DiscordActor")

  actor ! DiscordActor.StartDiscord(Config.token)
}
