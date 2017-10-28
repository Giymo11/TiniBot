package rip.hansolo.discord.tini.commands


import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import rip.hansolo.discord.tini.brain.TiniBrain

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.08.2016
  */
trait PrivateCommand extends Command {

  def exec(event: PrivateMessageReceivedEvent)

  override def registerCommand(): Unit = Future[Unit] {
    TiniBrain.registerPrivate(this)
    TiniBrain.register(this)
  }
}
