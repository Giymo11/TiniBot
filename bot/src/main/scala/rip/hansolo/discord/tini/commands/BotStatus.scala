package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import rip.hansolo.discord.tini.resources.Resources

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object BotStatus extends PrivateCommand {
  override def exec(event: PrivateMessageReceivedEvent): Unit = {
    val client = event.getJDA
    val content = event.getMessage.getContent.trim

    if( content.contains(Resources.authorPassword) ) {
      val status = content.replace("!botstatus", "").replace(Resources.authorPassword, "").trim
      client.getAccountManager.setGame(status)
      event.getMessage.getChannel.sendMessageAsync("status set", null)
    } else {
      event.getMessage.getChannel.sendMessageAsync("bli bla blub error ... :robot:", null)
    }
  }

  override def prefix: String = "!botstatus"

  /**
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    message.getChannel.sendMessageAsync(" *Tini listens to loud music and can't hear you screaming* :musical_note: ",null)
  }

  override def execHelp(args: String, message: Message): Unit = {
    message.getChannel.sendMessageAsync(getHelp,null)
  }

  override def getHelp: String = "`!botstatus` <password> <status> - Sets the Game status of Tini"
}
