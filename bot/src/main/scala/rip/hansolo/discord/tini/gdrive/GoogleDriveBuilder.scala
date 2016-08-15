package rip.hansolo.discord.tini.gdrive

import java.util

import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleCredential}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.{Drive, DriveScopes}

import scala.io.StdIn

/**
  * Created by: 
  *
  * @author Raphael
  * @version 14.08.2016
  */
object GoogleDriveBuilder {

  private val redirectURI = s"urn:ietf:wg:oauth:2.0:oob"

  private val clientID = System.getenv("GDRIVE_CLIENT_ID")
  private val secret = System.getenv("GDRIVE_SECRET")
  private val accessToken = System.getenv("GDRIVE_ACCESS_TOKEN")
  private val refreshToken = System.getenv("GDRIVE_REFRESH_TOKEN")

  private var drive: Option[Drive] = None

  /* Call this Method to acquire ACCESS and REFRESH Tokens */
  def main(argvs: Array[String]): Unit = {
    if( accessToken.isEmpty || refreshToken.isEmpty ) generateAccessTokens()
    else println("Nothing to do ...")
  }

  def generateAccessTokens(): Unit = {
    val http = new NetHttpTransport()
    val json = JacksonFactory.getDefaultInstance

    val authFlow = new GoogleAuthorizationCodeFlow.Builder(http, json, clientID, secret, util.Arrays.asList(DriveScopes.DRIVE)).build
    val uri = authFlow.newAuthorizationUrl().setRedirectUri(redirectURI).setAccessType("offline").build


    /* Can not be loaded by simple http request due javascript! */
    println("Open the URL: " + uri)
    println("Now enter the Authorization Code: ")
    val authCode = StdIn.readLine()

    val token = authFlow.newTokenRequest(authCode).setRedirectUri(redirectURI).execute

    println("GDRIVE_ACCESS_TOKEN: " + token.getAccessToken)
    println("GDRIVE_REFRESH_TOKEN: " + token.getRefreshToken)
  }

  def buildDrive: Drive = {
    val http = new NetHttpTransport()
    val json = JacksonFactory.getDefaultInstance

    val credential = new GoogleCredential.Builder()
      .setTransport(http)
      .setJsonFactory(json)
      .setClientSecrets(clientID,secret)
      .build

    credential.setAccessToken(accessToken)
    credential.setRefreshToken(refreshToken)


    drive = Some(new Drive.Builder(http,json,credential).setApplicationName("TiniBot").build)
    drive.get
  }

  def getDrive: Drive = drive.getOrElse(buildDrive)
}
