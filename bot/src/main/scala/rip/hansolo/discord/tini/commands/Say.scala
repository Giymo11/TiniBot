package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.resources.{LocalSettings, MessageData}

/**
  * Created by: 
  *
  * @author Raphael
  * @version 22.08.2016
  */
object Say extends Command {

  override def prefix: String = "say"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: MessageData)(implicit brain: LocalSettings): Unit = {
    message.getChannel.sendMessageAsync(args, null)
  }
}
