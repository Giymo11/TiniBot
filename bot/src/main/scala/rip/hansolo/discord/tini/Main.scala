package rip.hansolo.discord.tini

import java.time.temporal.ChronoUnit

import cats.data.Xor
import monix.eval.{Task, TaskApp}
import monix.execution.Cancelable
import sx.blah.discord.api.events.{Event, IListener}
import sx.blah.discord.api.{ClientBuilder, IDiscordClient}
import sx.blah.discord.handle.impl.events.{MessageReceivedEvent, ReadyEvent}
import sx.blah.discord.util.{DiscordException, MessageBuilder}

import scala.concurrent.Promise


/**
  * Created by Giymo11 on 08.08.2016.
  */
object Main extends TaskApp {

  import monix.execution.Scheduler.Implicits.global

  val clientTask = Task {
    new ClientBuilder().withToken(Resources.token).login()
  }

  /*val readyClientTask = clientTask.flatMap { client =>
    val willBeReady = Promise[Unit]

    client.getDispatcher.registerListener(new IListener[ReadyEvent] {
      override def handle(event: ReadyEvent): Unit = willBeReady.success()
    })

    Task.fromFuture(willBeReady.future).map(_ => client)
  }.memoize*/

  val readyClientTask = clientTask.flatMap { client =>
    Task.create[IDiscordClient] { (scheduler, callback) =>
      client.getDispatcher.registerListener(new IListener[ReadyEvent] {
        override def handle(event: ReadyEvent): Unit = callback.onSuccess(client)
      })
      Cancelable.empty
    }
  }.memoize


  val register = for(client <- readyClientTask) yield {

    val dispatch = client.getDispatcher

    dispatch.registerListener(new IListener[MessageReceivedEvent] {
      override def handle(event: MessageReceivedEvent): Unit = {
        if(event.getMessage.getChannel.isPrivate) {
          println(event.getMessage.getAuthor.getName + " sent me a message! It reads: " + event.getMessage.getContent)
          val content = event.getMessage.getContent
          if(content.contains("!kill") && content.contains(Resources.authorPassword)) {
            client.logout()
            System.exit(0)
          }
        }
      }
    })

    dispatch.registerListener(new IListener[MessageReceivedEvent] {
      override def handle(event: MessageReceivedEvent): Unit = {
        if(!event.getMessage.getChannel.isPrivate) {
          val message = event.getMessage
          val channel = message.getChannel
          println(channel.getGuild + " #" + channel.getName + " - @" + message.getAuthor.getName + ": " + message.getContent)
          val messageMaybe = Xor.catchNonFatal(new MessageBuilder(client).withContent("I agree :ok_hand:").withChannel(channel).send())
          for(myMessage <- messageMaybe) println("Sent response at " + myMessage.getTimestamp + ", after " + ChronoUnit.MILLIS.between(myMessage.getTimestamp, message.getTimestamp))
        }
      }
    })
  }

  override def runc: Task[Unit] = register
}


