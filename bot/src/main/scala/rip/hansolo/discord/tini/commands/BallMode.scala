package rip.hansolo.discord.tini.commands


import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.brain.TiniBrain
import rip.hansolo.discord.tini.resources.ShitTiniSays

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object BallMode extends Command {

  override def prefix: String = "8ballmode"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
    TiniBrain.is8ball.set(true)
    message.getChannel.sendMessage(ShitTiniSays.eightBallAnswer).queue()
  }
}
