package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.brain.TextBrainRegion

/**
  * Created by: 
  *
  * @author Raphael
  * @version 16.08.2016
  */
object Help extends Command {
  override def prefix: String = "help"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    val arguments = args.trim.split(" ").toList

    val emptyOrNil = (str: String) => str == null || str.isEmpty

    arguments match {
      case empty if empty.isEmpty || arguments.head.isEmpty =>
        val helpList = TextBrainRegion.channelCommands.map(_._2.shortHelp).filterNot(emptyOrNil)
        val msg = s"*Available Commands:*\n\n" + helpList.mkString("\n")
        message.getChannel.sendMessage(msg).queue()
      case "all" :: _ =>
        val helpList = TextBrainRegion.channelCommands.map(_._2.longHelp).filterNot(emptyOrNil)
        val msg = "***Available Commands with details:***\n\n" + helpList.mkString("\n")
        message.getChannel.sendMessage(msg).queue()
      case _ =>
        val msg = TextBrainRegion.channelCommands.getOrElse(arguments.head, NotACommand).longHelp
        message.getChannel.sendMessage(msg).queue()
    }
  }
}
