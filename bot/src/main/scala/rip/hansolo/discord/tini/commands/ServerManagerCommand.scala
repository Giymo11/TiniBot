package rip.hansolo.discord.tini.commands

import net.dv8tion.jda.Permission
import net.dv8tion.jda.entities.{Message, TextChannel}
import rip.hansolo.discord.tini.resources.LocalSettings

/**
	* Created by Giymo11 on 9/19/2016 at 11:29 PM.
	*/
trait ServerManagerCommand extends Command {

	/**
		* @param args    The return of its unapply. It's the String needed for the execution of the command
		*                Mostly here for convenience reasons, subject to change
		* @param message The message which
		*/
	override def exec(args: String, message: Message)(implicit brain: LocalSettings): Unit = {
		//message.getChannel.sendMessageAsync(":rolling_eyes:  *Tini won't change clothes here ...*", null)
		import scala.collection.JavaConverters._
		val author = message.getAuthor
		val channel = message.getChannel.asInstanceOf[TextChannel]
		val guild = channel.getGuild
		val roles = guild.getRolesForUser(author).asScala.toList
		val canManageServer = (for(role <- roles) yield role.hasPermission(Permission.MANAGE_SERVER)).fold(false)(_ || _)
		if(canManageServer)
			execute(args, message)
		else
			channel.sendMessageAsync("Tini won't change clothes for a plebeian like you! :crown:", null)
	}

	def execute(args: String, message: Message)(implicit brain: LocalSettings)
}
