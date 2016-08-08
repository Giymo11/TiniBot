package rip.hansolo.discord.tini

import java.time.temporal.ChronoUnit

import cats.data.Xor
import monix.eval.{Task, TaskApp}
import monix.execution.Cancelable
import net.dv8tion.jda.{JDA, JDABuilder}
import net.dv8tion.jda.events.ReadyEvent
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.hooks.ListenerAdapter


/**
  * Created by Giymo11 on 08.08.2016.
  */
object MainJDA extends TaskApp {

  import monix.execution.Scheduler.Implicits.global

  val clientReadyTask: Task[JDA] = Task.create[JDA] {
    (scheduler, callback) => {
      new JDABuilder()
        .setBotToken(Resources.token)
        // the stuff you want to react to
        .addListener(new ListenerAdapter {
          override def onReady(event: ReadyEvent): Unit = callback.onSuccess(event.getJDA)
        })
        .addListener(new ListenerAdapter {
          override def onGuildMessageReceived(event: GuildMessageReceivedEvent): Unit = {
            val channel = event.getChannel
            val message = event.getMessage
            val content = message.getContent
            println(channel.getGuild.getName + " #" + channel.getName + " - " + message.getTime + " - @" + message.getAuthor.getUsername + ": " + message.getContent)
            if(message.getAuthor.getId != event.getJDA.getSelfInfo.getId) {
              val messageMaybe = Xor.catchNonFatal(channel.sendMessage("I disagree :raised_hand:"))
              for(myMessage <- messageMaybe) println("Sent response at " + myMessage.getTime + ", after " + ChronoUnit.MILLIS.between(myMessage.getTime, message.getTime))
            }
          }
        })
        .addListener(new ListenerAdapter {
          override def onPrivateMessageReceived(event: PrivateMessageReceivedEvent): Unit = {
            val client = event.getJDA
            val content = event.getMessage.getContent
            if(content.contains("!kill") && content.contains(Resources.authorPassword))
              client.shutdown(true)
          }
        })
        .buildAsync()
      Cancelable.empty
    }
  }.memoize

  // the stuff you want to initiate yourself
  val work: Task[Unit] = for(client <- clientReadyTask) yield {
    import scala.collection.JavaConversions._
    for(guild <- client.getGuilds)
      println("I am in guild " + guild.getName)
  }

  override def runc: Task[Unit] = work
}
