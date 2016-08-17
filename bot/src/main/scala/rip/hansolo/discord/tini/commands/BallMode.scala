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
object BallMode extends Command {
  override def prefix: String = "!8ballmode"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    TiniBrain.is8ball.set(true)
    message.getChannel.sendMessageAsync(ShitTiniSays.agreement, null)
  }

  override def longHelp: String = shortHelp

  override def shortHelp: String = "`!8ballmode` - do that again."
}
