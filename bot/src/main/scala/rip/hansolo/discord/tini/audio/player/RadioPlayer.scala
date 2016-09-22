package rip.hansolo.discord.tini.audio.player

import java.io._
import java.util.Random
import java.util.concurrent.ConcurrentLinkedQueue
import javax.sound.sampled.{AudioFileFormat, AudioSystem}

import monix.eval.Task
import monix.execution.atomic.Atomic
import net.dv8tion.jda.audio.AudioConnection
import net.dv8tion.jda.entities.Guild
import rip.hansolo.discord.tini.audio.util.WebRadioFfmpegStream

import scala.concurrent.ExecutionContext.Implicits.global



/**
  * Created by: 
  *
  * @author Raphael
  * @version 28.08.2016
  */
class RadioPlayer(g: Guild) extends BasicPlayer(guild = g) {

  var buffersize = 5
  var ffmpeg: WebRadioFfmpegStream = _

  val ready = Atomic(false)
  val audioBuffer = new ConcurrentLinkedQueue[Array[Byte]]()

  override def play(resource: String): Unit = super.play(resource)

  override def load(resource: String): Unit = {
    ffmpeg = new WebRadioFfmpegStream(new Random().nextInt(40000)+20000,resource,audioBuffer, Task { doReadyState() })
    ffmpeg.streamReady.future.onSuccess { case x => doReadyState() }
  }

  private def doReadyState(): Unit = {
    if( audioBuffer.size() > buffersize ) {
      ready.set( true )
      play()
    } else {
      ffmpeg.bufferEmpty.set(true)
    }
  }

  override def getDuration: Double = Double.PositiveInfinity

  override def provide20MsAudio(): Array[Byte] = {
    if( audioBuffer.size() == 0 ) {
      ffmpeg.bufferEmpty.set(true)
      ready.set(false)
    }

    audioBuffer.poll()
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
}
