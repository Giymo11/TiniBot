package rip.hansolo.discord.tini



import monix.eval._
import monix.execution.Cancelable
import net.dv8tion.jda.core._
import rip.hansolo.discord.tini.brain._
import rip.hansolo.discord.tini.resources._
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.core.events.{Event, ReadyEvent}
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.requests.SessionReconnectQueue


/**
  * Created by Giymo11 on 08.08.2016.
  */
object Main extends TaskApp{

  val clientReadyTask: Task[JDA] = Task.create[JDA] {
    (scheduler, callback) => {

      CommandResolver.registerAllCommands()

      val jda = new JDABuilder(AccountType.BOT)
        .setToken(Reference.token)
        .setReconnectQueue(new SessionReconnectQueue())

      // here: the stuff you want to react to
      jda.addEventListener(
        new EventListener {
          override def onEvent(event: Event): Unit = event match {
            case _ : ReadyEvent => callback.onSuccess(event.getJDA)
            case _ => /* nothing that we are interested in ... */
          }
        },
        TextBrainRegion
      )

      jda.buildAsync()
      Cancelable.empty
    }
  }.memoize

  // here: the stuff you want to initiate yourself
  val work: Task[Unit] = for(client <- clientReadyTask) yield {
    import scala.collection.JavaConversions._
    for(guild <- client.getGuilds) {
      println("I am in guild " + guild.getName)
      val channel = guild.getPublicChannel

      if(TiniBrain.isSelfAccouncing.get) {
        channel.sendMessage(ShitTiniSays.selfAnnouncement).queue()
      }
    }
  }

  val gdriveLoader = Task {
    TiniBrain.isLoadingImages.set(true)
    println("FileNames. " + TiniBrain.filesWithNames.take(20).map(_._2.mkString(", ")).mkString("\n"))
    println("MimeTypes: " + TiniBrain.files.map(_.getMimeType).toSet.mkString("\n"))
    TiniBrain.isLoadingImages.set(false)
  }.runAsync

  override def runc: Task[Unit] = work.flatMap((_) => Task.fromFuture(TiniBrain.prophecy.future)) // weird hack, but so be it.
}
