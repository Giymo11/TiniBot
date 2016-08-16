package rip.hansolo.discord.tini.gdrive

import java.io.{IOException, InputStream}

import com.google.api.client.http.GenericUrl
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

import scala.collection.JavaConverters._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 14.08.2016
  */
class GoogleDrive(drive: Drive) {
  val folderType = "application/vnd.google-apps.folder"
  val fileType = "application/octet-stream"
  val imageType = "image/"

  def getRoot: File = drive.files.get("root").execute


  def getFolders(parent: File) = getDriveEntries(parent, folderType)

  def getFiles(parent: File) = getDriveEntries(parent, fileType)

  def getImages(parent: File) = getDriveEntries(parent, imageType)

  def getDriveEntries(parent: File, mimeType: String = null, limit: Int = 10000): List[File] = {
    if (mimeType == null) {
      drive.files.list.setQ(s"'%s' in parents and trashed = false".format(parent.getId))
        .setMaxResults(limit)
        .execute.getItems.asScala.toList
    } else {
      drive.files.list.setQ(s"'%s' in parents and mimeType contains '%s' and trashed = false".format(parent.getId, mimeType))
        .setMaxResults(limit)
        .execute.getItems.asScala.toList
    }
  }

  def getFileByPath(path: String): Option[File] = {
    val pathElements = path.split("/")
    var currentFile = getRoot

    for (file <- pathElements) {
      val f = getDriveEntries(currentFile).find(_.getTitle == file)

      if (f.isEmpty) return None
      else currentFile = f.get
    }

    Some(currentFile)
  }

  def getFileInputStream(file: File): Option[InputStream] = {
    if (file.getDownloadUrl == null || file.getDownloadUrl.isEmpty) return None

    try {
      Some(drive.getRequestFactory.buildGetRequest(new GenericUrl(file.getDownloadUrl)).execute.getContent)
    } catch {
      case io: IOException => None
    }
  }
}