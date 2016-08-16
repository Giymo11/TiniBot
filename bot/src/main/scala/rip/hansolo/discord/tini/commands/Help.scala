package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.brain.TiniBrain

/**
  * Created by: 
  *
  * @author Raphael
  * @version 16.08.2016
  */
object Help extends Command {
  override def prefix: String = "!help"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    message.getChannel.sendMessageAsync(s"*Available Commands:*\n" + TiniBrain.brain.commands.map(_._2.getHelp).mkString("\n"),null)
  }

  override def execHelp(args: String, message: Message): Unit = { }

  override def getHelp: String = prefix + " - Tells you how Tini works"
}
