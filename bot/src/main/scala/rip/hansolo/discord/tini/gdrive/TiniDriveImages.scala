package rip.hansolo.discord.tini.gdrive

import java.io.InputStream

import com.google.api.services.drive.model.File
import rip.hansolo.discord.tini.Util._
import rip.hansolo.discord.tini.brain.TiniBrain

/**
  * Created by: 
  *
  * @author Raphael
  * @version 15.08.2016
  */
object TiniDriveImages {

  val imageFolderName = System.getenv("TINI_GOOGLE_DRIVE")

  val gDrive = TiniBrain.gDrive

  val images = initializeImages()

  def initializeImages(): Vector[File] = {
    if( imageFolderName == null || imageFolderName.isEmpty ) {
      println("No Google Drive Path specified!")
      return Vector.empty
    }

    val drive = GoogleDriveBuilder.drive

    if(gDrive == null) println("gdrive null")

    val imageFolder = gDrive.searchPath(imageFolderName)

    println(imageFolder.map(_.getName))

    val images = imageFolder match {
      case Some(parent) =>
        gDrive.getFolders(parent).flatMap(gDrive.getImages)
      case None =>
        Seq()
    }

    println(s"Found ${images.size} Images in Google Drive Folder")
    images.toVector
  }

  def driveImageStream(maxSize: Long): Option[(InputStream, String)] = {
    println("strim plz")

    println(images.size)

    val smalls = images //.filter(_.getSize <= maxSize)
    println(smalls.size)

    val file = oneOf(
      smalls: _*
    )
    println("File: " + file.getName)

    gDrive.getFileInputStreamAndName {
      file
    }
  }

}
