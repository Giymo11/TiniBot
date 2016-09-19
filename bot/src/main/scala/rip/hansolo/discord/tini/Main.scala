package rip.hansolo.discord.tini


import monix.eval._
import monix.execution.{Cancelable, CancelableFuture}

import net.dv8tion.jda.events.ReadyEvent
import net.dv8tion.jda.hooks.ListenerAdapter
import net.dv8tion.jda._

import rip.hansolo.discord.tini.brain._
import rip.hansolo.discord.tini.resources._

import monix.execution.Scheduler.Implicits.global


/**
  * Created by Giymo11 on 08.08.2016.
  */
object Main extends TaskApp{

  val clientReadyTask: Task[JDA] = Task.create[JDA] {
    (scheduler, callback) => {

      CommandResolver.registerAllCommands()

      new JDABuilder()
        .setBotToken(Reference.token)
        // here: the stuff you want to react to
        .addListener(new ListenerAdapter { // TODO: this could probably be written in a more scala-esque way
          override def onReady(event: ReadyEvent): Unit = callback.onSuccess(event.getJDA)
        })
        .addListener(TextBrainRegion)
        .buildAsync()

      Cancelable.empty
    }
  }.memoize

  // here: the stuff you want to initiate yourself
  val work: Task[Unit] = for(client <- clientReadyTask) yield {
    import scala.collection.JavaConversions._
    for(guild <- client.getGuilds) {
      println("I am in guild " + guild.getName)

      SettingsBrain.init

      val channel = guild.getPublicChannel

      implicit val brain = SettingsBrain.getFor(guild.getId)

      if(brain.isSelfAnnouncing)
        channel.sendMessageAsync(ShitTiniSays.selfAnnouncement, null)
    }
  }

  // TODO: change stuff around so every channel can have his own way to access images on a gdrive
  val gdriveLoader: CancelableFuture[Unit] = Task {
    TiniBrain.isLoadingImages.set(true)
    println("FileNames. " + TiniBrain.filesWithNames.take(20).map(_._2.mkString(", ")).mkString("\n"))
    println("MimeTypes: " + TiniBrain.files.map(_.getMimeType).toSet.mkString("\n"))
    TiniBrain.isLoadingImages.set(false)
  }.runAsync

  override def runc: Task[Unit] = work.flatMap((_) => Task.fromFuture(TiniBrain.prophecy.future)) // weird hack, but so be it.
}
