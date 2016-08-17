package rip.hansolo.discord.tini.commands


import net.dv8tion.jda.entities.Message

import rip.hansolo.discord.tini.resources.ShitTiniSays

/**
  * Created by Giymo11 on 12.08.2016.
  */
object Catfacts extends Command {

  override def prefix: String = "!catfacts"

  /**
    *
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    val response = args match {
      case "" => ShitTiniSays.catfact
      case "credits" => ShitTiniSays.credits
      case _ => ShitTiniSays.catUsage
    }
    message.getChannel.sendMessageAsync(response, null)
  }

  override def execHelp(args: String, message: Message): Unit = ???

  override def getHelp: String =  prefix + " - I will show you some catfacts. :cat2:"
}
