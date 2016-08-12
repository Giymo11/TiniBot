package rip.hansolo.discord.tini.commands

import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.Util

/**
  * Created by Giymo11 on 12.08.2016.
  */
trait Command extends App {

  def prefix: String

  /**
    *
    * @param command the full command (excluding signal-character)
    * @return Some(args) with args being the parameter for the exec method. None if it did not match thisi Command
    */
  def unapply(command: String): Option[String] = command match {
    case valid if command.startsWith(prefix + " ") || command.startsWith(prefix + System.lineSeparator()) || command.startsWith(prefix + "\n") || command == prefix =>
      Some( command.drop(prefix.length).dropWhile(Util.isWhitespace).trim )
    case _ =>
      None
  }

  def matchesPrefix(command: String): Option[String] = command match {
    case valid if command.startsWith(prefix) =>
      Some( command.drop(prefix.length).dropWhile(Util.isWhitespace).trim )
    case _ =>
      None
  }

  /**
    *
    * @param args The return of its unapply. It's the String needed for the execution of the command
    *             Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  def exec(args: String, message: Message = null)
}
