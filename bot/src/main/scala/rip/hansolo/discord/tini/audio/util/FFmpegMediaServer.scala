package rip.hansolo.discord.tini.audio.util

import java.io.BufferedInputStream
import java.net.{ServerSocket, Socket, URL, URLConnection}

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
  class MediaInstance(p: Process,s: Socket) {

    def process: Process = p
    def socket: Socket = s

    def destroy(): Unit = {
      socket.close()
      process.destroy()
    }
  }
  class ProxyMediaInstance(process: Process,socket: Socket,proxySource: ProxyStream) extends MediaInstance(process,socket) {
    override def destroy(): Unit = {
      proxySource.destroy()
      super.destroy()
    }
  }

  private val serverSocket: ServerSocket = new ServerSocket(Reference.mediaServerPort)
  private val proxyServerSocket: ServerSocket = new ServerSocket(Reference.proxyServerProt)

  private val server: TrieMap[String,MediaInstance] = new TrieMap[String,MediaInstance]()
  private val canConnect = Atomic(true)

  def addMediaResource(name: String,resource: String,useProxy: Boolean = false): Promise[MediaInstance] = {
    val completion = Promise[MediaInstance]

    /* block call otherwise other instance might connect to our socket ... */
    if( !canConnect.get )
       canConnect.synchronized(
         canConnect.wait()
       )
    canConnect.set( false )

    var ffmpegSocket: Socket = null
    var proxySocket: Socket  = null
    var proxy: ProxyStream   = null

    val networkTask = Task {
      ffmpegSocket = this.serverSocket.accept()
    }.runAsync

    val ffmpegResource = if( useProxy ) s"tcp://127.0.0.1:${Reference.proxyServerProt}" else resource
    val mediaProxy: URLConnection  = useProxy match {
      case true => // Do proxy stuff
        val httpConnection = new URL( resource ).openConnection()
        httpConnection.setRequestProperty("user-agent",Reference.proxyUserAgent)
        httpConnection.connect()

        httpConnection
      case false => null
    }

    Task {
      if( useProxy ) {
        proxySocket = proxyServerSocket.accept()
        proxy = new ProxyStream(mediaProxy,proxySocket)
      }
    }.runAsync

    val ffmpegProcess: Process = startFFMPEGSlave(ffmpegResource,Reference.mediaServerPort)
    networkTask.onComplete { x =>
      /* notify other instances of release block */
      canConnect.set( true )
      canConnect.synchronized(
        canConnect.notifyAll()
      )

      /* register server */
      server.put( name, if( useProxy ) new ProxyMediaInstance(ffmpegProcess,ffmpegSocket,proxy) else new MediaInstance(ffmpegProcess,ffmpegSocket) )

      if( ffmpegProcess.isAlive ) completion.success( server(name) )
      else completion.failure( new RuntimeException("Something bad has happened and ffmpeg crashed") )
    }

    /* read stdout from ffmpeg (should be disabled in production mode) */
    Task {
      println("[MediaServer] Starting reading FFMPEG Stdout / Stderr")

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
      case Some(res) => res.destroy()
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
                               ,"-loglevel","debug"
                               ,"-nostdin"
                               ,"-stats"
                               //,"-hide_banner"
                               ,"-y"
                               ,"tcp://127.0.0.1:"+serverPort)

    val env = pb.environment()
    env.put("AV_LOG_FORCE_NOCOLOR","AV_LOG_FORCE_NOCOLOR")
    println("[MediaServer] Start Process: " + pb.command().asScala.mkString(" "))

    pb.start()
  }

}
