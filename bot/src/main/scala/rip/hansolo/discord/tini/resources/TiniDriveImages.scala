package rip.hansolo.discord.tini.resources

import java.nio.file.{Files, StandardCopyOption}

import com.google.api.services.drive.model.File
import rip.hansolo.discord.tini.gdrive.{GoogleDrive, GoogleDriveBuilder}

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * Created by: 
  *
  * @author Raphael
  * @version 15.08.2016
  */
object TiniDriveImages {

  val driveFolder = System.getenv("TINI_GOOGLE_DRIVE")
  val gDrive = new GoogleDrive(GoogleDriveBuilder.getDrive)
  val images = loadAllImages()

  def driveImage = downloadFile( getRandom( images ) )

  def loadAllImages(): List[File] = {
    if( System.getenv("TINI_GOOGLE_DRIVE") == null || System.getenv("TINI_GOOGLE_DRIVE").isEmpty ) {
      println("No Google Drive Path specified!")
      return List()
    }

    val collection = gDrive.getFileByPath(System.getenv("TINI_GOOGLE_DRIVE"))
    val images = new ListBuffer[File]()

    if( collection.isDefined ) for( folder <- gDrive.getFolders(collection.get) ) images ++= gDrive.getImages(folder)
    else return images.toList

    println(s"Found ${images.size} Images in Google Drive Folder")
    images.toList
  }

  def downloadFile(file: File): Option[java.io.File] = {
    val in = gDrive.getFileInputStream(file)
    if( in.isEmpty ) return None

    val tmp = java.io.File.createTempFile(file.getTitle,"."+file.getMimeType.split("/")(1))
    tmp.deleteOnExit()

    Files.copy(in.get,tmp.toPath,StandardCopyOption.REPLACE_EXISTING)
    Some(tmp)
  }

  def getRandom(list: List[File]) = list(Random.nextInt(list.size))

}
