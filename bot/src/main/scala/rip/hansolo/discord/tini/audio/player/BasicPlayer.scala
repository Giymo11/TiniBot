package rip.hansolo.discord.tini.audio.player

import javax.sound.sampled.{AudioFormat, AudioInputStream}

import com.sun.xml.internal.messaging.saaj.util.{ByteInputStream, ByteOutputStream}
import net.dv8tion.jda.audio.player.Player
import net.dv8tion.jda.entities.Guild

import scala.concurrent.Promise

/**
  * Created by: 
  *
  * @author Raphael
  * @version 26.08.2016
  */
abstract class BasicPlayer(guild: Guild) extends Player {

  private var isRegisterd = false
  private var playing,paused,stopped = false
  private var audioStream: AudioInputStream = _

  def play(resource: String): Promise[Unit] = ???
  def load(resource: String): Unit = ???

  def load(byteOutputStream: ByteOutputStream,audioFMT: AudioFormat): Unit = {
    val inStream = new ByteInputStream(byteOutputStream.getBytes,byteOutputStream.getCount)
    audioStream  = new AudioInputStream(inStream, audioFMT, byteOutputStream.getCount)
    audioStream.close()
  }

  def play(byteOutputStream: ByteOutputStream,audioFMT: AudioFormat): Unit = {
    load(byteOutputStream,audioFMT)
    play()
  }

  override def stop(): Unit = {
    playing = false
    stopped = true

    guild.getAudioManager.closeAudioConnection()

    println("Player was stopped!")
  }
  override def play(): Unit = {
    setAudioSource( audioStream )
    println(s"Length: $getDuration sec")

    if( !isRegisterd ) {
      guild.getAudioManager.setSendingHandler(this)
      isRegisterd = true
    }

    playing = true
    stopped = false
    paused = false

/*    Task { this.stop() }
      .delayExecution( (this.getDuration+1) seconds )
      .runAsync*/
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
