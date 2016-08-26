package rip.hansolo.discord.tini.audio.player

import javax.sound.sampled.{AudioFormat, AudioInputStream}

import com.sun.xml.internal.messaging.saaj.util.{ByteInputStream, ByteOutputStream}
import net.dv8tion.jda.JDA
import net.dv8tion.jda.audio.player.Player

import scala.concurrent.Promise

/**
  * Created by: 
  *
  * @author Raphael
  * @version 26.08.2016
  */
abstract class BasicPlayer(api: JDA) extends Player {

  private var isRegisterd = false
  private var ply: Promise[Unit] = _
  private var playing,paused,stopped = false
  private var audioStream: AudioInputStream = _

  def play(resource: String): Promise[Unit] = ???
  def play(byteOutputStream: ByteOutputStream,audioFMT: AudioFormat): Promise[Unit] = {
    val inStream = new ByteInputStream(byteOutputStream.getBytes,byteOutputStream.getBytes.length)
    audioStream  = new AudioInputStream(inStream, audioFMT, byteOutputStream.getBytes.length)
    audioStream.close()

    play()
    ply = Promise[Unit]
    ply
  }

  override def stop(): Unit = {
    playing = false
    stopped = true

    ply.success()
    println("Player was stopped!")
  }
  override def play(): Unit = {
    setAudioSource( audioStream )
    println(s"Length: $getDuration sec")

    playing = true
    stopped = false
    paused = false
  }
  override def restart(): Unit = {
    setAudioSource( this.audioStream )

    playing = true
    stopped = false
    paused = false

    println("Player was restarted!")
  }
  override def pause(): Unit ={
    playing = false
    stopped = false
    paused = true

    println("Player was paused!")
  }

  override def isPaused: Boolean = paused
  override def isStopped: Boolean = stopped
  override def isStarted: Boolean = true
  override def isPlaying: Boolean = playing

  def destroy(): Unit = audioStream.close()
  def getDuration: Double = (audioStream.getFrameLength+0.0) / audioStream.getFormat.getFrameRate

}
