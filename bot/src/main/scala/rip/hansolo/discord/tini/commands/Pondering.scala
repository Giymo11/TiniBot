package rip.hansolo.discord.tini.commands

import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.resources.{LocalSettings, MessageData, ShitTiniSays}

/**
	* Created by Giymo11 on 9/22/2016 at 6:26 PM.
	*/
object Pondering extends Command {
	override def prefix: String = "pondering"

	/**
		* @param args    The return of its unapply. It's the String needed for the execution of the command
		*                Mostly here for convenience reasons, subject to change
		* @param message The message which
		*/
	override def exec(args: String, message: MessageData)(implicit brain: LocalSettings): Unit = {
		message.getChannel.sendMessageAsync(ShitTiniSays.pondering, null)
	}
}
