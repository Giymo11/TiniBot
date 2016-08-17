package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.brain.TiniBrain
import rip.hansolo.discord.tini.resources.ShitTiniSays

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object Shutup extends Command {
  override def prefix: String = "shutup"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    TiniBrain.is8ball.set(false)
    Repeat.shutup()

    message.getChannel.sendMessageAsync(ShitTiniSays.shutupResponse,null)
  }

  override def longHelp: String = shortHelp
  override def shortHelp: String = s"$command - not do that anymore."
}
