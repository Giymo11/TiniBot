package rip.hansolo.discord.tini.commands


import java.io._

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import com.mashape.unirest.request.body.MultipartBody
import monix.eval.Task
import monix.execution.Cancelable
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.impl.JDAImpl
import net.dv8tion.jda.core.entities.{EntityBuilder, Message, MessageChannel}
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.requests.Requester
import org.apache.http.entity.ContentType
import org.json._
import rip.hansolo.discord.tini.Util._
import rip.hansolo.discord.tini.brain.TiniBrain
import rip.hansolo.discord.tini.resources.ShitTiniSays

import scala.collection.JavaConverters._


/**
  * Created by: 
  *
  * @author Raphael
  * @version 15.08.2016
  */
object DriveImage extends Command {

  override def prefix: String = "image"

  /**
    *
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message, event: GuildMessageReceivedEvent): Unit = {

    Task.create[Unit] { (_, _) =>
      if(TiniBrain.isLoadingImages.get)
        message.getChannel.sendMessage("I am still loading...").queue()
      else {
        message.getChannel.sendTyping().queue()

        println("args: " + args)

        val streamWithName = args.split(" ").toList match {
          case none if none.isEmpty =>
            driveImageStream(maxSize = 8 << 20)
          case mimeType :: Nil =>
            driveImageStream(maxSize = 8 << 20, mimeType)
          case mimeType :: tags =>
            val filteredTags = argsToTags(tags.mkString(" "))
            println(filteredTags.mkString(", "))
            driveImageStream(maxSize = 8 << 20, mimeType, filteredTags)
        }

        def getResponseMessage(tags: Seq[String]) =
          new MessageBuilder()
            .append(ShitTiniSays.imageResponse + getTagsString(tags))
            .build()
        def getTagsString(tags: Seq[String]) = if(TiniBrain.isShowingTags.get) "\nTags: " + tags.mkString(", ") else ""

        streamWithName match {
          case Some( (fileStream, name, tags) ) =>
            val msg = new MessageBuilder()
              .setTTS(false)
              .append("There you go!")
              .build()
            //message.getChannel.sendFile(fileStream, name, msg).queue()
            sendFile(message.getChannel, fileStream, getResponseMessage(tags), name)
            fileStream.close()
          case None =>
            message.getChannel.sendMessage("No files found :(").queue()
            println("No files found :(")
        }
      }
      Cancelable.empty
    }.runAsync
  }

  def argsToTags(args: String) = {
    val parts = args.split("\"")
    println(parts.mkString(", "))
    parts
      .zipWithIndex
      .flatMap {
        case (part, index) =>
          if(index % 2 == 1)
            Seq(part)
          else
            part.split(" ").filter(!_.isEmpty)
      }
  }

  def driveImageStream(maxSize: Long, mimeType: String = "", tags: Seq[String] = Seq()): Option[(InputStream, String, Seq[String])] = {

    println("Size: " + TiniBrain.imagesWithNames.size)
    println("Mimetype: " + mimeType)
    println("Tags: " + tags)

    val realMime = if(mimeType == null || mimeType == "all") "" else mimeType

    // TODO: make sure only small images are tried to be sent
    val x = TiniBrain
      .imagesWithNames
      .filter{ case (file, folders) => file.getMimeType.contains(realMime) }
      .filter{ case (file, folders) => tags.isEmpty || folders.exists(tags.contains(_)) }

    println(x.size)

    x match {
      case Vector() => None
      case files =>
        val (file, tags) = oneOf(files: _*)
        for(key <- file.getUnknownKeys.keySet().asScala) println(key)

        val stream = TiniBrain.gDrive.getFileInputStreamAndName(file)
        stream.map( (_, file.getName, tags) )
    }
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
        .append("There you go!")
        .build()
      else message

    try{
      val body: MultipartBody = Unirest.post(
        Requester.DISCORD_API_PREFIX + "channels/" + channel.getId + "/messages")
        .header("authorization", channel.getJDA.getToken)
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
