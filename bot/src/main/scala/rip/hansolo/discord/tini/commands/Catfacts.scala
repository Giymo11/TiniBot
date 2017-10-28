package rip.hansolo.discord.tini.commands


import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.resources.ShitTiniSays

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
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    val response = args match {
      case "" => ShitTiniSays.catfact
      case "credits" => credits
      case _ => longHelp
    }
    message.getChannel.sendMessage(response).queue()
  }

  val credits: String = config.getString("credits")
}
