package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.resources.ShitTiniSays

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object Bofh extends Command {

  override def prefix: String = "bofh"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
      message.getChannel.sendMessageAsync(ShitTiniSays.bofh,null)
  }

  override def longHelp: String = shortHelp
  override def shortHelp: String = s"`$command` - Gives you a Bastard Operator from Hell Excuse"
}
