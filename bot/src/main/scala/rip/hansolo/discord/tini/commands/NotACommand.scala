package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object NotACommand extends Command {
  override def prefix: String = "!<this-is-not-a-tini-command>!"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {}

  override def longHelp: String = ""
  override def shortHelp: String = ""
}
