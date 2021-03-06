package rip.hansolo.discord.tini.commands


import com.typesafe.config.Config

import net.dv8tion.jda.entities.Message
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent


/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object NotACommand extends PrivateCommand {
  override def prefix: String = "this-is-not-a-tini-command"

  override lazy val config: Config = null
  override def longHelp: String = ""
  override def shortHelp: String = ""

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    val msg = s"Tini is confused, there is no such a command: \n$args\nType `${Help.command}` to see the commands"
    message.getChannel.sendMessageAsync(msg, null)
  }

  override def exec(event: PrivateMessageReceivedEvent): Unit = exec(null, event.getMessage)
}
