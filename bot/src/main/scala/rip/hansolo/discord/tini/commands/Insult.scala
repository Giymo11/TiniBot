package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.resources.ShitTiniSays

/**
  * Created by Giymo11 on 8/27/2016 at 1:27 AM.
  */
object Insult extends Command {
  override def prefix: String = "insult"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit =
    message.getChannel.sendMessageAsync(ShitTiniSays.insult, null)
}
