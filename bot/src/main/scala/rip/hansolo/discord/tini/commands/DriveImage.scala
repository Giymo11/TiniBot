package rip.hansolo.discord.tini.commands


import java.io._

import scala.collection.JavaConverters._

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import com.mashape.unirest.request.body.MultipartBody

import monix.eval.Task
import monix.execution.Cancelable

import net.dv8tion.jda._
import net.dv8tion.jda.entities._
import net.dv8tion.jda.entities.impl._
import net.dv8tion.jda.handle.EntityBuilder
import net.dv8tion.jda.requests.Requester

import org.apache.http.entity.ContentType
import org.json._

import rip.hansolo.discord.tini.brain.TiniBrain
import rip.hansolo.discord.tini.Util._

import monix.execution.Scheduler.Implicits.global


/**
  * Created by: 
  *
  * @author Raphael
  * @version 15.08.2016
  */
object DriveImage extends Command {

  override def prefix: String = "!image"

  /**
    *
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {

    Task.create[Unit] { (_, _) =>
      if(TiniBrain.isLoadingImages.get) {
        message.getChannel.sendMessage("I am still loading...")
      } else {

        message.getChannel.sendTyping()

        println("gimme img plz")
        val maybe = driveImageStream(maxSize = 8 << 20)
        if(maybe.isDefined) {
          val (fileStream, name) = maybe.get
          message.getChannel.sendMessage("Sending " + name)
          sendFile(message.getChannel, fileStream, null, name)
          fileStream.close()
        } else {
          message.getChannel.sendMessage("Error opening the File :( ")
          println("Error opening the File :( ")
        }
      }
      Cancelable.empty
    }.runAsync
  }

  def driveImageStream(maxSize: Long): Option[(InputStream, String)] = {

    // TODO: make sure only small images are tried to be sent
    val smalls = TiniBrain.images //.filter(_.getSize <= maxSize)

    val file = oneOf(
      smalls: _*
    )

    for(key <- file.getUnknownKeys.keySet().asScala) println(key)

    TiniBrain.gDrive.getFileInputStreamAndName { file }
  }

  /**
    * Mostly copied from the JDA TextChannelImpl.
    */
  def sendFile(channel: MessageChannel, fileStream: InputStream, message: Message, filename: String): Message = {

    /*
    if (file.length > (8 << 20)) //8MB
      throw new IllegalArgumentException("File is to big! Max file-size is 8MB")
    */

    val api: JDAImpl = channel.getJDA.asInstanceOf[JDAImpl]

    val realMessage = if(message == null)
      new MessageBuilder()
        .setTTS(false)
        .appendString("There you go!")
        .build()
      else message

    try{
      val body: MultipartBody = Unirest.post(
        Requester.DISCORD_API_PREFIX + "channels/" + channel.getId + "/messages")
        .header("authorization", channel.getJDA.getAuthToken)
        .header("user-agent", Requester.USER_AGENT)
        .field("content", realMessage.getRawContent)
        .field("tts", realMessage.isTTS)
        .field("file", fileStream, ContentType.APPLICATION_OCTET_STREAM, filename)

      val dbg: String =
        s"""Requesting ${body.getHttpRequest.getHttpMethod.name} -> ${body.getHttpRequest.getUrl}
           |	Payload: file: $filename,
           |  message: ${if (message == null) "null" else message.getRawContent},
           |   tts: ${if (message == null) "N/A" else message.isTTS}
           |	Response: """.stripMargin

      val requestBody: String = body.asString.getBody
      Requester.LOG.trace(dbg + body)
      try {
        val messageJson: JSONObject = new JSONObject(requestBody)
        return new EntityBuilder(api).createMessage(messageJson)
      } catch {
        case e: JSONException =>
          Requester.LOG.fatal("Following json caused an exception: " + requestBody)
          Requester.LOG.log(e)
      }
    } catch {
      case e: UnirestException =>
        Requester.LOG.log(e)
    }
    null
  }
}
