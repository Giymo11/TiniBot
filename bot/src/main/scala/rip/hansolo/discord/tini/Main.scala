package rip.hansolo.discord.tini


import monix.eval.{Task, TaskApp}
import monix.execution.Cancelable
import net.dv8tion.jda.events.ReadyEvent
import net.dv8tion.jda.hooks.ListenerAdapter
import net.dv8tion.jda.{JDA, JDABuilder}
import rip.hansolo.discord.tini.brain._
import rip.hansolo.discord.tini.gdrive.GoogleDrive
import rip.hansolo.discord.tini.resources._

/**
  * Created by Giymo11 on 08.08.2016.
  */
object Main extends TaskApp{

  val clientReadyTask: Task[JDA] = Task.create[JDA] {
    (scheduler, callback) => {
      if( !Util.isEnvSet("TINI_TOKEN") || !Util.isEnvSet("TINI_PASSWORD") )
        callback.onError(new RuntimeException("TINI_TOKEN or TINI_PASSWORD is not set!"))

      new JDABuilder()
        .setBotToken(Resources.token)
        // here: the stuff you want to react to
        .addListener(new ListenerAdapter { // TODO: this could probably be written in a more scala-esque way
          override def onReady(event: ReadyEvent): Unit = callback.onSuccess(event.getJDA)
        })
        .addListener(new TextBrainRegion)
        .buildAsync()

      Cancelable.empty
    }
  }.memoize

  // here: the stuff you want to initiate yourself
  val work: Task[Unit] = for(client <- clientReadyTask) yield {
    import scala.collection.JavaConversions._
    for(guild <- client.getGuilds) {
      println("I am in guild " + guild.getName)
      val channel = guild.getPublicChannel
      channel.sendMessageAsync(ShitTiniSays.selfAnnouncement, null)
    }
    println("Not loading!")
    TiniBrain.isLoadingImages.set(true)
    val folders = GoogleDrive.getFolders(TiniBrain.files)
    //println(folders.map(_.getName).mkString("\n"))
    println("MimeTypes: " + TiniBrain.files.map(_.getMimeType).toSet.mkString("\n"))
    TiniBrain.isLoadingImages.set(false)
    println("Not loading!")
  }

  override def runc: Task[Unit] = work.flatMap((_) => Task.fromFuture(TiniBrain.prophecy.future)) // weird hack, but so be it.
}
