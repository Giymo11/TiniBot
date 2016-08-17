package rip.hansolo.discord.tini.commands


import net.dv8tion.jda.entities.Message

import rip.hansolo.discord.tini.Util
import rip.hansolo.discord.tini.brain.TiniBrain

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Giymo11 on 12.08.2016.
  */
trait Command {

  // TODO: rewrite `Command` to be a class, making the objects currently inheriting only instances of this class.
  // TODO: Register to TiniBrain in constructor.

  def prefix: String

  /**
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
    * @param args The return of its unapply. It's the String needed for the execution of the command
    *             Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  def exec(args: String, message: Message = null)

  def longHelp: String

  def shortHelp: String

  def registerCommand(): Unit = Future[Unit] { TiniBrain.register(this) }
}

object Command {
  def unapply(command: String): Option[(Command, String)] = command match {
    case Bio(args) => Some(Bio, args)
    case Roll(args) => Some(Roll, args)
    case Catfacts(args) => Some(Catfacts, args)
    case Imitate(args) => Some(Imitate, args)
    case DriveImage(args) => Some(DriveImage, args)
    case Repeat(args) => Some(Repeat, args)
    case _ => None
  }
}
