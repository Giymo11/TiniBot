package rip.hansolo.discord.tini.gdrive


import scala.collection.JavaConverters._
import scala.io.StdIn

import com.google.api.client.googleapis.auth.oauth2._
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive._

import rip.hansolo.discord.tini.Util
import rip.hansolo.discord.tini.resources.Reference


/**
  * Created by: 
  *
  * @author Raphael
  * @version 14.08.2016
  */
object GoogleDriveBuilder {

  private val redirectURI = s"urn:ietf:wg:oauth:2.0:oob" // can only be used for type "Other UI" applications.

  val appName = "TiniBot"
  val credentialsDirFile: java.io.File = new java.io.File(Reference.gdriveCredentialsDir)
  val dataStoreFactors = new FileDataStoreFactory(credentialsDirFile)
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

    val user = "user" // TODO: substitute with actual ID

    // TODO: read client secrets from json
    val authFlow = new GoogleAuthorizationCodeFlow.Builder(
      httpTransport,
      jsonFactory,
      Reference.gdriveClientID,
      Reference.gdriveSecret,
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
