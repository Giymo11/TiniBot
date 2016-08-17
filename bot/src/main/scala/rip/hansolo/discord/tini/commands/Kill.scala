package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import rip.hansolo.discord.tini.brain.TiniBrain
import rip.hansolo.discord.tini.resources.Resources

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object Kill extends PrivateCommand {
  override def prefix: String = "kill"

  override def longHelp: String = shortHelp
  override def shortHelp: String = s"`$command <password>` - Kills Tini :cry:"

  override def exec(event: PrivateMessageReceivedEvent): Unit = {
    val client = event.getJDA
    val content = event.getMessage.getContent.trim

    if( content.contains(Resources.authorPassword) ) {
      client.shutdown(true)
      TiniBrain.killYourself()
    } else {
      event.getMessage.getChannel.sendMessageAsync("Not today :robot:",null)
    }
  }

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    message.getChannel.sendMessageAsync(" *Too many People are watching, you can't kill Tini here* ",null)
  }
}
