package rip.hansolo.discord.tini.gdrive

import java.io.{IOException, InputStream}

import com.google.api.client.http.GenericUrl
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

import scala.collection.JavaConverters._
import scala.util.Try

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

  def getDriveEntries(parent: File, mimeType: String = null, limit: Int = 1000): Seq[File] = {

    val part2 =if(mimeType == null || mimeType.isEmpty) "" else s"and mimeType contains '$mimeType'"

    val query = s"'${parent.getId}' in parents and trashed = false $part2"

    println(query)

      drive.files.list.setQ(query)
      .setPageSize(limit)
      .execute.getFiles.asScala
  }


  def searchPath(path: String): Option[File] = {
    val results = searchPathInParent(path.dropWhile(char => char == '/').split("/"), "\"root\"")
    results.headOption
  }

  def searchPathInParent(pathElements: Seq[String], parentId: String): Seq[File] = {

    val fields = "files(fullFileExtension,id,imageMediaMetadata(height,width),lastModifyingUser/displayName,mimeType,name,size,trashed,webContentLink),nextPageToken"

    def searchFolder(parendId: String, folderName: String, pageToken: String = null, mimeType: String = null): Seq[File] = {

      import scala.collection.JavaConverters._

      val para = "name = \"" + folderName + "\" and " + parentId + " in parents and trashed = false" + (if(mimeType != null) s" and mimeType contains $mimeType" else "")

      println(para)

      val query = drive.files.list()
        .setQ(para)
        .setFields(fields)
        .setPageSize(100)

      val files = if(pageToken == null)
        query.execute()
      else
        query.setPageToken(pageToken).execute()

      if(files.getNextPageToken == null)
        files.getFiles.asScala
      else
        files.getFiles.asScala ++ searchFolder(parentId, folderName, files.getNextPageToken, mimeType)
    }

    if(pathElements.size == 1) {
      searchFolder(parentId, pathElements.head)
    } else {

      val results = searchFolder(parentId, pathElements.head, mimeType = folderType)
      for(result <- results) {
        val moreResults = searchPathInParent(pathElements.tail, result.getId)
        if(moreResults != null && moreResults.nonEmpty) // eager!
          return moreResults
      }
      Seq()
    }
  }

  def getFileInputStreamAndName(file: File): Option[(InputStream, String)] = {
    Try(
      (drive.files().get(file.getId).executeMediaAsInputStream(),
        file.getName)
    ).toOption
  }
}