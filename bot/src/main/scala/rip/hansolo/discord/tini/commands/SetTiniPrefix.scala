package rip.hansolo.discord.tini.commands


import net.dv8tion.jda.entities._
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.Permission
import rip.hansolo.discord.tini.Util
import rip.hansolo.discord.tini.brain._
import rip.hansolo.discord.tini.resources._


/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
object SetTiniPrefix extends ServerManagerCommand with PrivateCommand {

  override def prefix: String = "setTiniPrefix"

  override def execute(args: String, message: Message)(implicit brain: LocalSettings): Unit =
    doIt(message.getRawContent, message.getChannel)

  override def exec(event: PrivateMessageReceivedEvent)(implicit brain: LocalSettings): Unit = {
    doIt(event.getMessage.getRawContent, event.getChannel)
  }

  def doIt(rawContent: String, channel: MessageChannel)(implicit brain: LocalSettings): Unit = {
    val newPrefix = rawContent
      .drop(brain.tiniPrefix.length)
      .dropWhile(Util.isWhitespace)
      .drop(prefix.length)
      .dropWhile(Util.isWhitespace)
    SettingsBrain.update(brain.copy(tiniPrefix = newPrefix))

    channel.sendMessageAsync("New prefix: " + newPrefix, null)
    println(newPrefix)
  }
}
