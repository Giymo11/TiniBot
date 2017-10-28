package rip.hansolo.discord.tini.commands


import com.google.firebase.database.{DataSnapshot, DatabaseError, DatabaseReference, ValueEventListener}
import com.typesafe.config.Config
import net.dv8tion.jda.core.entities.{Message, MessageChannel, User}
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import rip.hansolo.discord.tini.brain.TiniBrain

import scala.collection.JavaConverters._


/**
  * Created by Giymo11 on 11.08.2016.
  */

object Bio extends Command {

  override def prefix: String = "bio"

  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = args match {
    case Bio.Set(arg) =>
      Bio.Set.exec(arg, message,event)
    case Bio.Get(_) =>
      Bio.Get.exec(null, message,event)
    case _ =>
      sendUsage(message.getChannel)
  }

  def sendUsage(channel: MessageChannel): Unit = channel.sendMessage(longHelp).queue()

  def bioOf(user: User): DatabaseReference = TiniBrain.users.child(user.getId + "/bio")

  object Set extends Command {

    def prefix = "set"

    override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {
      val author = message.getAuthor
      val channel = message.getChannel

      // syntax errors if not referenced the "java" way ?
      val errorCallback = new DatabaseReference.CompletionListener {
        override def onComplete(databaseError: DatabaseError, databaseReference: DatabaseReference): Unit = {
          Option(databaseError) match {
            case Some(error) =>
              channel.sendMessage(
                s"""There was an error setting your Bio! :(
                   |${databaseError.getMessage}
                   |${databaseError.getDetails}""".stripMargin).queue()

            case None =>
              channel.sendMessage("Bio updated successfully!").queue()
          }
        }
      }

      bioOf(author).setValue(args, errorCallback)
    }

    override lazy val config: Config = null
    override def longHelp: String = shortHelp
    override def shortHelp: String = s"`${Bio.command} $prefix <biography text>` - Sets your biography"
  }

  object Get extends Command{

    override def prefix: String = "@"

    /**
      *
      * @param command the full command (excluding signal-character)
      * @return Some(args) with args being the parameter for the exec method. None if it did not match this Command
      */
    override def unapply(command: String): Option[String] = matchesPrefix(command)

    override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {

      val channel = message.getChannel

      val otherMentions: List[User] = {
        val mentions = message.getMentionedUsers.asScala.toList

        val isSelf = (user: User) => user.getId == message.getJDA.getSelfUser.getId
        mentions.filterNot(isSelf)
      }

      class BioEventListener(user: User) extends ValueEventListener {
        override def onDataChange(dataSnapshot: DataSnapshot) {
          println(dataSnapshot.getValue)
          channel.sendMessage(s"Bio of ${user.getAsMention} is:\n" + dataSnapshot.getValue(classOf[String])).queue()
        }
        override def onCancelled(databaseError: DatabaseError) {
          channel.sendMessage(s"Bio of ${user.getAsMention} could not be read.").queue()
        }
      }

      otherMentions match {
        case user :: Nil =>
          bioOf(user).addListenerForSingleValueEvent(new BioEventListener(user))
        case _ =>
          sendUsage(channel)
      }
    }
    override lazy val config: Config = null
    override def longHelp: String = shortHelp
    override def shortHelp: String = s"`${Bio.command} <@user>` - Gets the Bio of the User"
  }
}


