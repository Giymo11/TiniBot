package rip.hansolo.discord.tini.gdrive

import java.io.InputStream
import java.lang.Error

import com.google.api.services.drive.model.File
import rip.hansolo.discord.tini.Util
import rip.hansolo.discord.tini.Util._
import rip.hansolo.discord.tini.brain.TiniBrain

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

/**
  * Created by: 
  *
  * @author Raphael
  * @version 15.08.2016
  */
object TiniDriveImages {

  val imageFolderName = System.getenv("TINI_GOOGLE_DRIVE")
  val folderType = "application/vnd.google-apps.folder"
  val fileType = "application/octet-stream"
  val imageType = "image/"

  val gDrive = TiniBrain.gDrive

  val files = initializeFiles()

  val images = getImages(files)

  def getImages(files: Seq[File]) = files.filter(file =>
    file.getMimeType != null && file.getMimeType.contains(imageType)
  )
  def getFolders(files: Seq[File]) = files.filter(_.getMimeType == folderType)

  def initializeFiles(): Vector[File] = {
    if( !Util.isEnvSet("TINI_GOOGLE_DRIVE") ) {
      println("No Google Drive Path specified!")
      return Vector.empty
    }

    val drive = GoogleDriveBuilder.drive

    if(gDrive == null) println("gdrive null")

    val imageFolder = gDrive.searchPath(imageFolderName)

    println(imageFolder.map(_.getName))

    def getFilesForParent(parentFile: File): Seq[File] = Try(gDrive.searchFolder(parentFile.getId)) match {
      case Success(someFiles) =>
        val files = someFiles.filter(_ != null).filter(!_.getName.contains("TINI_NO"))
        files ++ getFolders(files).flatMap(getFilesForParent)
      case Failure(exception) =>
        println(exception.getMessage + " for file " + parentFile.getName)
        Seq()
    }

    val images2 = imageFolder match {
      case Some(parentFile) =>
        getFilesForParent(parentFile)
      case None =>
        println("no files found")
        Seq()
    }

    println("others")
/*
    val images = imageFolder match {
      case Some(parent) =>
        gDrive.getFolders(parent).flatMap(gDrive.getImages)
      case None =>
        Seq()
    }*/

    //Limit files to file size
    images2.filter(_.size() < (8 << 20) ).toVector
  }

  def driveImageStream(maxSize: Long): Option[(InputStream, String)] = {
    val smalls = images //.filter(_.getSize <= maxSize)


    val file = oneOf(
      smalls: _*
    )
    //println("A file has no size...")
    //println("File: " + file.getName + ", size: " + (file.getSize >> 10) + ", by: " + file.getSharingUser.getDisplayName)
    for(key <- file.getUnknownKeys.keySet().asScala) println(key)

    gDrive.getFileInputStreamAndName {
      file
    }
  }

}
