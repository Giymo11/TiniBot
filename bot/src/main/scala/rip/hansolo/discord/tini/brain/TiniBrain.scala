package rip.hansolo.discord.tini.brain


import java.io._

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.firebase._

import scala.concurrent.Promise
import monix.execution.atomic.Atomic
import rip.hansolo.discord.tini.commands._
import rip.hansolo.discord.tini.gdrive._
import rip.hansolo.discord.tini.resources.Resources

import scala.concurrent.Promise


/**
  * Contains the state of Tini's Brain
  */
object TiniBrain {

  val brain = new TextBrainRegion
  def register(command: Command) = {
    println("command registered: " + command)
    brain.commands.put(command.prefix,command)
  }

  /**
    * If this promise is fulfilled, Tini will kill itself and take the JVM with her
    */
  val prophecy = Promise[Unit]

  val is8ball = Atomic(false)
  val isLoadingImages = Atomic(true)
  val isShowingTags = Atomic(false)

  def killYourself() = prophecy.success()

  // no lazy because we want to know about failures at startup!
  val firebaseApp = {
    val options = new FirebaseOptions.Builder()
      // if you want your own credentials here, follow the Guide at the firebase docs and then make sure to give the
      // ServiceAccount "Project -> Editor" permissions in the IAM settings (under Permissions in Firebase Console)
      .setServiceAccount(new FileInputStream("credentials/tinibot-firebase.json"))
      .setDatabaseUrl("https://tinibot-a4c07.firebaseio.com/")
      .build()

    FirebaseApp.initializeApp(options)
  }
  val firebaseDatabase = FirebaseDatabase.getInstance()
  val users = firebaseDatabase.getReference("users")

  val gDrive = new GoogleDrive(GoogleDriveBuilder.drive)


  val filesWithNames = gDrive.initializeFiles(Resources.gdriveFolderName)
  val files = filesWithNames.map{ case (file, parents) => file }

  val imagesWithNames = filesWithNames.filter{ case (file, parents) => GoogleDrive.isImage(file) }
  val images = files.filter(GoogleDrive.isImage)
}
