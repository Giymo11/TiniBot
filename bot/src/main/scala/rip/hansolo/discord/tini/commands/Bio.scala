package rip.hansolo.discord.tini.commands

import com.google.firebase.database.DatabaseReference.CompletionListener
import com.google.firebase.database.{DataSnapshot, DatabaseError, DatabaseReference, ValueEventListener}
import net.dv8tion.jda.entities.{MessageChannel, User}
import rip.hansolo.discord.tini.brain.TiniBrain

/**
  * Created by Giymo11 on 11.08.2016.
  */

object Bio {

  def unapply(arg: String): Option[String] = arg match {
    case bioCommand if bioCommand.startsWith("!bio") =>
      Some( arg.drop("!bio".length).trim )
    case _ =>
      None
  }

  object Set {

    def unapply(command: String): Option[String] = command match {
      case bioaddCommand if bioaddCommand.startsWith("set ") =>
        Some( bioaddCommand.drop("set ".length).trim )
      case _ =>
        None
    }

    def setBio(arg: String, author: User, channel: MessageChannel): Unit = {
      val users = TiniBrain.firebaseDatabase.getReference("users")
      val id = author.getId

      import rip.hansolo.discord.tini.Util._
      val errorCallback: CompletionListener = (dbError: DatabaseError, dbRef: DatabaseReference) =>
        Option(dbError) match {
          case Some(error) => channel.sendMessageAsync("There was an error setting your Bio! :( \n" + dbError.getMessage + "\n" + dbError.getDetails, null)
          case None => channel.sendMessageAsync("Bio updated successfully!", null)
        }

      users.child(id).child("bio").setValue(arg, errorCallback)
    }
  }

  object Get {

    def unapply(command: String): Option[String] = command match {
      case bioaddCommand if bioaddCommand.startsWith("@") =>
        Some( bioaddCommand )
      case _ =>
        None
    }

    def tellBio(user: User, channel: MessageChannel): Unit = {

      class BioEventListener() extends ValueEventListener {
        override def onDataChange(dataSnapshot: DataSnapshot) {
          println(dataSnapshot.getValue)
          channel.sendMessageAsync(s"Bio of ${user.getAsMention} is:\n" + dataSnapshot.getValue(classOf[String]), null)
        }
        override def onCancelled(databaseError: DatabaseError) {
          channel.sendMessageAsync(s"Bio of ${user.getAsMention} could not be read.", null)
        }
      }

      TiniBrain.users.child(s"${user.getId}/bio").addListenerForSingleValueEvent(new BioEventListener)
    }
  }
}


