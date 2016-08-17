package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.brain.{TextBrainRegion, TiniBrain}

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
    val arguments = args.split(" ")

    if( arguments.nonEmpty && arguments.head != "all" ) {
      message.getChannel.sendMessageAsync(TextBrainRegion.channelCommands.getOrElse(arguments.head, NotACommand).longHelp, null)
    } else if( arguments.nonEmpty ) {
      message.getChannel.sendMessageAsync(TextBrainRegion.channelCommands.map(_._2.longHelp).mkString("\n\n"),null)
    } else {
      message.getChannel.sendMessageAsync(s"*Available Commands:*\n" + TextBrainRegion.channelCommands.map(_._2.shortHelp).mkString("\n"), null)
    }
  }

  override def longHelp: String = "`!help [command]` - Tini tells you how to use the command"
  override def shortHelp: String = prefix + " - Tells you how Tini works, use !help <command> to get more help with one command\n" +
    "You can also use `!help all` to get the detailed help from all commands"
}
