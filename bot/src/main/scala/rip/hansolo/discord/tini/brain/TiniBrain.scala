package rip.hansolo.discord.tini.brain


import java.io._

import scala.concurrent.Promise

import monix.execution.atomic.Atomic

import com.google.api.services.drive.model.File

import com.google.firebase.database._
import com.google.firebase._

import rip.hansolo.discord.tini.commands._
import rip.hansolo.discord.tini.gdrive._
import rip.hansolo.discord.tini.resources.Reference




/**
  * Contains the state of Tini's Brain
  */
object TiniBrain {
  val isLoadingImages = Atomic(true)


  def register(command: Command): Unit = TextBrainRegion.channelCommands.put(command.prefix, command)
  def registerPrivate(command: PrivateCommand): Unit = TextBrainRegion.privateCommands.put(command.prefix, command)

  /**
    * If this promise is fulfilled, Tini will kill itself and take the JVM with her
    */
  val prophecy: Promise[Unit] = Promise[Unit]


  def killYourself(): prophecy.type = prophecy.success()

  // no lazy because we want to know about failures at startup!
  val firebaseApp: FirebaseApp = {
    val options = new FirebaseOptions.Builder()
      // if you want your own credentials here, follow the Guide at the firebase docs and then make sure to give the
      // ServiceAccount "Project -> Editor" permissions in the IAM settings (under Permissions in Firebase Console)
      .setServiceAccount(new FileInputStream(Reference.firebaseJson))
      .setDatabaseUrl(Reference.firebaseUrl)
      .build()

    FirebaseApp.initializeApp(options)
  }
  val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
  val users: DatabaseReference = firebaseDatabase.getReference("users")

  val gDrive = new GoogleDrive(GoogleDriveBuilder.drive)


  val filesWithNames: Vector[(File, Seq[String])] = gDrive.initializeFiles(Reference.gdriveFolderName)
  val files: Vector[File] = filesWithNames.map{ case (file, parents) => file }

  val imagesWithNames: Vector[(File, Seq[String])] = filesWithNames.filter{ case (file, parents) => GoogleDrive.isImage(file) }
  val images: Vector[File] = files.filter(GoogleDrive.isImage)
}
