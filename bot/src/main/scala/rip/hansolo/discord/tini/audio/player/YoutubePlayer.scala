package rip.hansolo.discord.tini.audio.player

import java.io.BufferedInputStream
import java.net.{InetSocketAddress, Proxy, URL, URLConnection}
import javax.sound.sampled.AudioFormat

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import net.dv8tion.jda.JDA
import net.dv8tion.jda.entities.Guild
import net.dv8tion.jda.requests.Requester
import net.sourceforge.jaad.aac.{Decoder, SampleBuffer}
import net.sourceforge.jaad.mp4.MP4Container
import net.sourceforge.jaad.mp4.api.AudioTrack
import org.apache.http.HttpHost

import scala.concurrent.Promise

/**
  * Created by: 
  *
  * @author Raphael
  * @version 26.08.2016
  */
class YoutubePlayer(guild: Guild) extends BasicPlayer( guild = guild ) {
  private var duration = 0.0

  override def play(resource: String): Unit = {
    val conn = getJDAConnection(new URL(resource))
    val bufferedStream = new BufferedInputStream(conn.getInputStream)

    //none MP4 Files -> Bot is dead!
    val mp4Container = new MP4Container(bufferedStream)
    val track: AudioTrack = mp4Container.getMovie.getTracks(AudioTrack.AudioCodec.AAC).get(0).asInstanceOf[AudioTrack]
    val audioFMT = new AudioFormat(track.getSampleRate, track.getSampleSize, track.getChannelCount, true, true)

    this.duration = mp4Container.getMovie.getDuration

    /* local testing ... */
    /*val line = javax.sound.sampled.AudioSystem.getSourceDataLine(audioFMT)
    line.open()
    line.start()
    */

    val sBuffer = new ByteOutputStream()
    val decoder = new Decoder(track.getDecoderSpecificInfo)

    while( track.hasMoreFrames ) {
      val frame = track.readNextFrame()
      val buffer = new SampleBuffer()

      decoder.decodeFrame(frame.getData,buffer)
      sBuffer.write( buffer.getData )
      //line.write(buffer.getData,0,buffer.getData.length)
    }
    sBuffer.close()

    this.play(sBuffer,audioFMT)
  }

  override def load(resource: String): Unit = {
    val conn = getJDAConnection(new URL(resource))
    val bufferedStream = new BufferedInputStream(conn.getInputStream)

    //none MP4 Files -> Bot is dead!
    println("Loading stream ...")
    val mp4Container = new MP4Container(bufferedStream)
    val track: AudioTrack = mp4Container.getMovie.getTracks(AudioTrack.AudioCodec.AAC).get(0).asInstanceOf[AudioTrack]
    val audioFMT = new AudioFormat(track.getSampleRate, track.getSampleSize, track.getChannelCount, true, true)

    this.duration = mp4Container.getMovie.getDuration

    /* local testing ... */
    val line = javax.sound.sampled.AudioSystem.getSourceDataLine(audioFMT)
    line.open()
    line.start()


    val sBuffer = new ByteOutputStream()
    val decoder = new Decoder(track.getDecoderSpecificInfo)

    while( track.hasMoreFrames ) {
      val frame = track.readNextFrame()
      val buffer = new SampleBuffer()

      decoder.decodeFrame(frame.getData,buffer)
      sBuffer.write( buffer.getData )
      line.write(buffer.getData,0,buffer.getData.length)
    }

    println("Stream loaded and converted")

    sBuffer.close()
    load(sBuffer,audioFMT)
  }



  override def getDuration: Double = this.duration
}
