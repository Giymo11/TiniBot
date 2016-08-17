package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import rip.hansolo.discord.tini.brain.TiniBrain
import rip.hansolo.discord.tini.resources.Reference

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object SetChar extends PrivateCommand {

  override def prefix: String = "setChar"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    message.getChannel.sendMessageAsync("*:rolling_eyes:  Tini won't change clothes here ... *",null)
  }

  override def longHelp: String = s"`$command <password> <char>` - Sets the Command char for Tini"
  override def shortHelp: String = longHelp

  override def exec(event: PrivateMessageReceivedEvent): Unit = {
    val args = event.getMessage.getContent.trim.split(" ")
    if( args.length >= 2 && args(0) == Reference.authorPassword ) {
      TiniBrain.prefixChar.set(args(1)(0))
    } else {
      event.getMessage.getChannel.sendMessageAsync("Tini can't set the command prefix :sad:",null)
    }
  }
}
