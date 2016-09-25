package rip.hansolo.discord.tini.audio.util

import java.net.{ServerSocket, Socket}

import monix.eval.Task
import monix.execution.atomic.Atomic
import monix.execution.Scheduler.Implicits.global
import rip.hansolo.discord.tini.resources.Reference

import scala.collection.concurrent.TrieMap
import scala.collection.JavaConverters._
import scala.concurrent.Promise
import scala.io.Source

/**
  * Created by: 
  *
  * @author Raphael
  * @version 24.09.2016
  */
object FFmpegMediaServer {
  case class MediaInstance(process: Process,socket: Socket)

  private val serverSocket: ServerSocket = new ServerSocket(Reference.mediaServerPort)

  private val server: TrieMap[String,MediaInstance] = new TrieMap[String,MediaInstance]()
  private val canConnect = Atomic(true)

  def addMediaResource(name: String,resource: String): Promise[MediaInstance] = synchronized {
    val completion = Promise[MediaInstance]

    /* block call otherwise other instance might connect to our socket ... */
    if( !canConnect.get )
       canConnect.synchronized(
         canConnect.wait()
       )
    canConnect.set( false )

    var ffmpegSocket: Socket = null
    val networkTask = Task {
      ffmpegSocket = this.serverSocket.accept()
    }.runAsync

    val ffmpegProcess: Process = startFFMPEGSlave(resource,Reference.mediaServerPort)
    networkTask.onComplete { x =>
      /* notify other instances of release block */
      canConnect.set( true )
      canConnect.synchronized(
        canConnect.notifyAll()
      )

      /* register server */
      server.put( name, MediaInstance(ffmpegProcess,ffmpegSocket) )
      completion.success( server(name) )
    }

    /* read stdout from ffmpeg (should be disabled in production mode) */
    Task {
      println("starting reading IO")

      try {
        while (ffmpegProcess.isAlive)
          Source.fromInputStream(ffmpegProcess.getErrorStream).foreach(print)
      } catch {
        case all: Exception => all.printStackTrace()
      }

      println("[MediaServer] Stdout was closed!")
    }.runAsync

    completion
  }

  def deleteMediaResource(name: String): Unit = {
    server.get(name) match {
      case Some(res) =>
        res.socket.close()
        res.process.destroy()
      case None => /* ok, i'm done already */
    }

    server.remove(name)
  }

  def getMediaResource(name: String): Option[MediaInstance] = server.get(name)

  private def startFFMPEGSlave(resource: String,serverPort: Int): Process = {
    val pb = new ProcessBuilder(Reference.ffmpegBinary
                               ,"-i",resource
                               ,"-acodec","pcm_s16be"
                               ,"-vn"
                               ,"-ar","48000"
                               ,"-b:a","96k"
                               ,"-f","s16be"
                               ,"-loglevel","quiet"
                               ,"-nostdin"
                               ,"-stats"
                               ,"-hide_banner"
                               ,"-y"
                               ,"tcp://127.0.0.1:"+serverPort)

    val env = pb.environment()
    env.put("AV_LOG_FORCE_NOCOLOR","AV_LOG_FORCE_NOCOLOR")
    println("[MediaServer] Start Process: " + pb.command().asScala.mkString(" "))

    pb.start()
  }

}
