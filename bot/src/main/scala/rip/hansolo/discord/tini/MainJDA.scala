package rip.hansolo.discord.tini

import java.time.temporal.ChronoUnit

import cats.data.Xor
import monix.eval.{Task, TaskApp}
import monix.execution.Cancelable
import monix.execution.atomic.Atomic
import net.dv8tion.jda.entities.Message
import net.dv8tion.jda.{JDA, JDABuilder}
import net.dv8tion.jda.events.ReadyEvent
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.hooks.ListenerAdapter

import scala.concurrent.Promise


/**
  * Created by Giymo11 on 08.08.2016.
  */
object MainJDA extends TaskApp{

  import monix.execution.Scheduler.Implicits.global

  val done = Promise[Unit]

  val isAsked = Atomic(true)

  val clientReadyTask: Task[JDA] = Task.create[JDA] {
    (scheduler, callback) => {
      new JDABuilder()
        .setBotToken(Resources.token)
        // the stuff you want to react to
        .addListener(new ListenerAdapter {
          override def onReady(event: ReadyEvent): Unit = callback.onSuccess(event.getJDA)

          override def onGuildMessageReceived(event: GuildMessageReceivedEvent): Unit = {
            val channel = event.getChannel
            val message = event.getMessage
            val content = message.getContent
            // TODO: Use a logger
            println(channel.getGuild.getName + " #" + channel.getName + " - " + message.getTime + " - @" + message.getAuthor.getUsername + ": " + message.getContent)

            if(message.getAuthor.getId != event.getJDA.getSelfInfo.getId) {
              import scala.compat.java8.FunctionConverters._

              val timer = (myMessage: Message) => println("Sent response at " + myMessage.getTime + ", after " + ChronoUnit.MILLIS.between(myMessage.getTime, message.getTime))

              // TODO: add error handling!
              // TODO: clean up and make dynamic
              message.getContent match {
                case "!help" =>
                  channel.sendMessageAsync(ShitTiniSays.help, asJavaConsumer(timer))
                case "!shutup" =>
                  isAsked.set(false)
                  channel.sendMessageAsync(ShitTiniSays.shutupResponse, asJavaConsumer(timer))
                case "!8ballmode" =>
                  isAsked.set(true)
                  channel.sendMessageAsync(ShitTiniSays.agreement, asJavaConsumer(timer))
                case "!catfacts" =>
                  channel.sendMessageAsync(ShitTiniSays.catfact, asJavaConsumer(timer))
                case "!catfacts credits" =>
                  channel.sendMessageAsync(ShitTiniSays.credits, asJavaConsumer(timer))
                case _ if isAsked.get =>
                  channel.sendMessageAsync(ShitTiniSays.agreement, asJavaConsumer(timer))
                case _ => ()
              }
            } else println("sent myself")
          }

          override def onPrivateMessageReceived(event: PrivateMessageReceivedEvent): Unit = {
            val client = event.getJDA
            val content = event.getMessage.getContent
            if(content.contains("!kill") && content.contains(Resources.authorPassword)) {
              client.shutdown(true)
              done.success()
            }
          }
        })
        .buildAsync()
      Cancelable.empty
    }
  }.memoize

  // the stuff you want to initiate yourself
  val work: Task[Unit] = for(client <- clientReadyTask) yield {
    import scala.collection.JavaConversions._
    for(guild <- client.getGuilds) {
      println("I am in guild " + guild.getName)
      val channel = guild.getPublicChannel
      channel.sendMessageAsync(ShitTiniSays.selfAnnouncement, null)
    }
  }

  override def runc: Task[Unit] = work.flatMap((_) => Task.fromFuture(done.future)) // weird hack, but so be it.
}
