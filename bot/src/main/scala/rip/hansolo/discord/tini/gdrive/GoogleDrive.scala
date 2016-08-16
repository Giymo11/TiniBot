package rip.hansolo.discord.tini.gdrive


import java.io.InputStream

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File


object GoogleDrive {

  val folderType = "application/vnd.google-apps.folder"
  val fileType = "application/octet-stream"
  val imageType = "image/"

  def getImages(files: Seq[File]) = files.filter(file =>
    file.getMimeType != null && file.getMimeType.contains(imageType)
  )
  def getFolders(files: Seq[File]) = files.filter(_.getMimeType == folderType)
}

/**
  * Created by: 
  *
  * @author Raphael
  * @version 14.08.2016
  */
class GoogleDrive(drive: Drive) {

  def getFileFromPath(path: String): Option[File] = searchPathInParent(path.dropWhile(_ == '/').split("/"), "root").headOption

  def searchPathInParent(pathElements: Seq[String], parentId: String): Seq[File] = pathElements.toList match {
    case Nil =>
      searchFolder(parentId)
    case head :: Nil =>
      searchFolder(parentId, head)
    case head :: tail =>
      val files = searchFolder(parentId, head, mimeType = GoogleDrive.folderType)
      for (result <- files) {
        val children = searchPathInParent(tail, result.getId)
        if (children != null && children.nonEmpty) // eager!
          return children
      }
      Seq()
  }

  private def searchFolder(parentId: String, fileName: String = null, pageToken: String = null, mimeType: String = null): Seq[File] = {

    val fields = "files(fullFileExtension,id,imageMediaMetadata(height,width),lastModifyingUser/displayName,sharingUser/displayName,mimeType,name,size,trashed,webContentLink),nextPageToken"

    val nameQuery = if(fileName == null) "" else s"name = '$fileName' and "
    val mimeTypeQuery = if (mimeType != null) s" and mimeType contains '$mimeType'" else ""

    val para = nameQuery + s"'$parentId' in parents and trashed = false" + mimeTypeQuery

    println(para)

    val query = drive.files.list()
      .setQ(para)
      .setFields(fields)
      .setPageSize(1000)

    val files = if(pageToken == null)
      query.execute()
    else
      query.setPageToken(pageToken).execute()

    if(files.getNextPageToken == null)
      files.getFiles.asScala
    else
      files.getFiles.asScala ++ searchFolder(parentId, fileName, files.getNextPageToken, mimeType)
  }

  def getFilesForParent(parentFile: File): Seq[File] = Try(searchFolder(parentFile.getId)) match {
    case Success(someFiles) =>
      val files = someFiles.filter(_ != null).filter(!_.getName.contains("TINI_NO"))
      files ++ GoogleDrive.getFolders(files).flatMap(getFilesForParent)
    case Failure(exception) =>
      println(exception.getMessage + " for file " + parentFile.getName)
      Seq()
  }

  def getFileInputStreamAndName(file: File): Option[(InputStream, String)] = {
    Try(
      (drive.files().get(file.getId).executeMediaAsInputStream(),
        file.getName)
    ).toOption
  }

  def initializeFiles(folderPath: String): Vector[File] = {
    if( folderPath == null || folderPath.isEmpty ) {
      println("No Google Drive Path specified!")
      Vector.empty
    } else getFileFromPath(folderPath) match {
      case Some(parentFile) =>
        println("Master folder: " + parentFile.getName)
        val files = getFilesForParent(parentFile)
        println(s"Found ${files.size} Files in Google Drive Folder")
        files.toVector
      case None =>
        println("no files found")
        Vector.empty
    }
  }
}

