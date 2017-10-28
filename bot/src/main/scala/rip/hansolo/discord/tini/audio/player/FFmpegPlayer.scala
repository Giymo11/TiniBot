package rip.hansolo.discord.tini.audio.player

import java.io.InputStream

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.execution.atomic.Atomic
import net.dv8tion.jda.core.audio.AudioConnection
import net.dv8tion.jda.core.entities.Guild
import rip.hansolo.discord.tini.audio.util.FFmpegMediaServer
import rip.hansolo.discord.tini.audio.util.FFmpegMediaServer.MediaInstance

import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by: 
  *
  * @author Raphael
  * @version 28.08.2016
  */
class FFmpegPlayer(g: Guild, useHTTPProxy: Boolean = false) extends BasicPlayer(guild = g) {

  val bufferTime = 5000
  val ready = Atomic(false)
  var stream: InputStream = _
  var media: MediaInstance = _

  override def play(resource: String): Unit = super.play(resource)

  override def load(resource: String): Promise[Unit] = {
    val state = Promise[Unit]

    Task.fromFuture(FFmpegMediaServer.addMediaResource(g.getId,resource,useHTTPProxy).future)
      .delayExecution( bufferTime millisecond )
      .runAsync
      .andThen {
        case Success(mediaData) =>
          stream = mediaData.socket.getInputStream

          //Thread.sleep( bufferTime ) // slow streams may have to buffer some data
          ready.set( true )

          media = mediaData
          state.success()
        case Failure(ext) => state.failure(ext)
      }

    state
  }

  override def getDuration: Double = Double.PositiveInfinity

  override def provide20MsAudio(): Array[Byte] = {
    val d = new Array[Byte](AudioConnection.OPUS_FRAME_SIZE * 4)

    if( stream.available() < AudioConnection.OPUS_FRAME_SIZE * 4 * 2 ) {
      System.err.println("Warning Stream buffer is very low: " + stream.available() + " Bytes")
    }

    if( stream.read( d ) != AudioConnection.OPUS_FRAME_SIZE * 4 ) {
      System.err.println("Stream stopped, could not read enough data ...")
      stop()
    }

    d
  }

  override def canProvide: Boolean = ready.get

  override def isOpus: Boolean = false

  override def play(): Unit = {
    if( !isRegisterd ) {
      g.getAudioManager.setSendingHandler(this)
      isRegisterd = true
    }

    playing = true
    stopped = false
    paused = false
  }

  override def stop(): Unit = {

    FFmpegMediaServer.deleteMediaResource(g.getId)
    ready.set( false )

    super.stop()
  }
}
