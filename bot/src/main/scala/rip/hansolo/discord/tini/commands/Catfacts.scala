package rip.hansolo.discord.tini.commands


import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.resources.{LocalSettings, Reference, ShitTiniSays}

/**
  * Created by Giymo11 on 12.08.2016.
  */
object Catfacts extends Command {

  override def prefix: String = "catfacts"

  /**
    *
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message)(implicit brain: LocalSettings): Unit = {
    val response = args match {
      case "" => ShitTiniSays.catfact
      case "credits" => credits
      case _ => longHelp
    }
    message.getChannel.sendMessageAsync(response, null)
  }

  val credits = config.getString("credits")
}
