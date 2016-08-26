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
  val userAgent: String = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 " + Requester.USER_AGENT;
  private var duration = 0.0

  override def play(resource: String): Promise[Unit] = {
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
    load(sBuffer,audioFMT)
  }

  private def getJDAConnection(urlOfResource: URL): URLConnection = {
    var conn: URLConnection  = null
    val jdaProxy: HttpHost   = guild.getJDA.getGlobalProxy

    if (jdaProxy != null)
    {
      val proxyAddress = new InetSocketAddress(jdaProxy.getHostName, jdaProxy.getPort)
      val proxy = new Proxy(Proxy.Type.HTTP, proxyAddress)
      conn = urlOfResource.openConnection(proxy)
    } else {
      conn = urlOfResource.openConnection()
    }

    if (conn == null)
      throw new IllegalArgumentException("The provided URL resulted in a null URLConnection! Does the resource exist?")

    //conn.setConnectTimeout(10)
    //conn.setReadTimeout(10)
    conn.setRequestProperty("user-agent", userAgent)
    conn.connect()

    conn
  }

  override def getDuration: Double = this.duration
}
