package rip.hansolo.discord.tini.brain


import java.io.{BufferedReader, FileInputStream, InputStreamReader}

import cats.data.Xor
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.{FirebaseApp, FirebaseOptions}

import scala.concurrent.Promise
import monix.execution.atomic.Atomic


/**
  * Contains the state of Tini's Brain
  */
object TiniBrain {

  /**
    * If this promise is fulfilled, Tini will kill itself and take the JVM with her
    */
  val prophecy = Promise[Unit]
  val is8ball = Atomic(true)

  def killYourself() = prophecy.success()

  val firebaseApp = {

    val options = new FirebaseOptions.Builder()
      // if you want your own credentials here, follow the Guide at the firebase docs and then make sure to give the
      // ServiceAccount "Project -> Editor" permissions in the IAM settings (under Permissions in Firebase Console)
      .setServiceAccount(new FileInputStream("tinibot-firebase.json"))
      .setDatabaseUrl("https://tinibot-a4c07.firebaseio.com/")
      .build()

    FirebaseApp.initializeApp(options)
  }
  val firebaseDatabase = FirebaseDatabase.getInstance()
  val users = firebaseDatabase.getReference("users")
}
