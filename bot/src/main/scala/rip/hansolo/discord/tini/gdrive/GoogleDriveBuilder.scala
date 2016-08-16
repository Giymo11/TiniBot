package rip.hansolo.discord.tini.gdrive

import java.util

import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleCredential}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.{Drive, DriveScopes}

import scala.io.StdIn
import ammonite.ops._
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.util.store.FileDataStoreFactory
import rip.hansolo.discord.tini.Util

import scala.collection.JavaConverters._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 14.08.2016
  */
object GoogleDriveBuilder {

  private val redirectURI = s"urn:ietf:wg:oauth:2.0:oob" // can only be used for type "Other UI" applications.

  private val clientID = System.getenv("GDRIVE_CLIENT_ID")
  private val secret = System.getenv("GDRIVE_SECRET")

  val appName = "TiniBot"
  val credentialsDir: java.io.File = new java.io.File("drive-credentials")
  val dataStoreFactors = new FileDataStoreFactory(credentialsDir)
  val jsonFactory = JacksonFactory.getDefaultInstance
  val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
  val scopes = Seq(DriveScopes.DRIVE).asJava

  val drive: Drive = {
    val credential = authorize()

    new Drive.Builder(
      httpTransport,
      jsonFactory,
      credential
    ).setApplicationName(appName)
      .build
  }

  // TODO: add more than one user to be authorized
  def authorize(): Credential = {

    if( Util.isEnvSet("GDRIVE_CLIENT_ID") ) println("No GDrive Client ID!")
    if( Util.isEnvSet("GDRIVE_SECRET") ) println("No GDrive Secret!")

    val user = "user" // TODO: substitute with actual ID

    // TODO: read client secrets from json
    val authFlow = new GoogleAuthorizationCodeFlow.Builder(
      httpTransport,
      jsonFactory,
      clientID,
      secret,
      scopes
    ).setDataStoreFactory(dataStoreFactors)
      .setAccessType("offline")
      .build

    Option( authFlow.loadCredential(user) ).getOrElse {
      val uri = authFlow
        .newAuthorizationUrl()
        .setRedirectUri(redirectURI)
        .setAccessType("offline")
        .build

      println("Open this URL and copy the Authorization Token: " + uri)

      val authCode = StdIn.readLine()
      val token = authFlow.newTokenRequest(authCode).setRedirectUri(redirectURI).execute

      authFlow.createAndStoreCredential(token, user)
    }
  }
}
