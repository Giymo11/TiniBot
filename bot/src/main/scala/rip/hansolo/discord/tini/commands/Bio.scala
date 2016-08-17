package rip.hansolo.discord.tini.commands

import com.google.firebase.database.DatabaseReference.CompletionListener
import com.google.firebase.database._
import net.dv8tion.jda.entities._
import rip.hansolo.discord.tini.brain.TiniBrain
import rip.hansolo.discord.tini.resources.ShitTiniSays

import rip.hansolo.discord.tini.Util._

/**
  * Created by Giymo11 on 11.08.2016.
  */

object Bio extends Command {

  override def prefix: String = "!bio"

  override def exec(args: String, message: Message): Unit = args match {
    case Bio.Set(arg) =>
      Bio.Set.exec(arg, message)
    case Bio.Get(_) =>
      Bio.Get.exec(null, message)
    case _ =>
      sendUsage(message.getChannel)
  }

  def sendUsage(channel: MessageChannel): Unit = channel.sendMessageAsync(ShitTiniSays.bioUsage, null)

  def bioOf(user: User): DatabaseReference = TiniBrain.users.child(user.getId + "/bio")


  object Set extends Command {

    def prefix = "set"

    override def exec(args: String, message: Message): Unit = {
      val author = message.getAuthor
      val channel = message.getChannel

      val errorCallback: CompletionListener = (dbError: DatabaseError, dbRef: DatabaseReference) =>
        Option(dbError) match {
          case Some(error) =>
            channel.sendMessageAsync(
              s"""There was an error setting your Bio! :(
                 |${dbError.getMessage}
                 |${dbError.getDetails}""".stripMargin, null)
          case None =>
            channel.sendMessageAsync("Bio updated successfully!", null)
        }

      bioOf(author).setValue(args, errorCallback)
    }
  }

  object Get extends Command{

    override def prefix: String = "@"

    /**
      *
      * @param command the full command (excluding signal-character)
      * @return Some(args) with args being the parameter for the exec method. None if it did not match this Command
      */
    override def unapply(command: String): Option[String] = matchesPrefix(command)

    override def exec(args: String, message: Message): Unit = {

      val channel = message.getChannel

      val otherMentions: List[User] = {
        import scala.collection.JavaConverters._
        val mentions = message.getMentionedUsers.asScala.toList

        val isSelf = (user: User) => user.getId == message.getJDA.getSelfInfo.getId
        mentions.filterNot(isSelf)
      }

      class BioEventListener(user: User) extends ValueEventListener {
        override def onDataChange(dataSnapshot: DataSnapshot) {
          println(dataSnapshot.getValue)
          channel.sendMessageAsync(s"Bio of ${user.getAsMention} is:\n" + dataSnapshot.getValue(classOf[String]), null)
        }
        override def onCancelled(databaseError: DatabaseError) {
          channel.sendMessageAsync(s"Bio of ${user.getAsMention} could not be read.", null)
        }
      }

      otherMentions match {
        case user :: Nil =>
          bioOf(user).addListenerForSingleValueEvent(new BioEventListener(user))
        case _ =>
          sendUsage(channel)
      }
    }
  }
}


