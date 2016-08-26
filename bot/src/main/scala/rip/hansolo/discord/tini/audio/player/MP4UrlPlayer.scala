package rip.hansolo.discord.tini.audio.player

import java.io.BufferedInputStream
import java.net.{InetSocketAddress, Proxy, URL, URLConnection}
import javax.sound.sampled.{AudioFormat, AudioInputStream, AudioSystem}

import com.sun.xml.internal.messaging.saaj.util.{ByteInputStream, ByteOutputStream}
import net.dv8tion.jda.JDA
import net.dv8tion.jda.audio.player.Player
import net.dv8tion.jda.requests.Requester
import net.sourceforge.jaad.aac.{Decoder, SampleBuffer}
import net.sourceforge.jaad.mp4.MP4Container
import net.sourceforge.jaad.mp4.api.AudioTrack
import org.apache.http.HttpHost

import scala.collection.JavaConverters._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 26.08.2016
  */
class MP4UrlPlayer(api: JDA,urlOfResource: URL) extends Player {
  val userAgent: String = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 " + Requester.USER_AGENT;
  var playing: Boolean = false


  override def stop(): Unit = playing = false

  override def isPaused: Boolean = !playing

  // Copy of URLPlayer
  private def getConn(): URLConnection = {
    var conn: URLConnection  = null
    val jdaProxy: HttpHost   = api.getGlobalProxy

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

  override def play(): Unit = {
    val conn = getConn()
    var sBuffer = new ByteOutputStream()

    println("Getting Input stream ...")
    val rawStream = conn.getInputStream
    val bufferedStream = new BufferedInputStream(rawStream)
    println("available (estimated) bytes: " + rawStream.available())

    //none MP4 Files -> Bot is dead!
    val mp4Container = new MP4Container(bufferedStream)
    println("Read stream in MP4Container ...")


    mp4Container.getMovie.getTracks.asScala.foreach( x => println("Found Track: " + x.getType))

    val track: AudioTrack = mp4Container.getMovie.getTracks(AudioTrack.AudioCodec.AAC).get(0).asInstanceOf[AudioTrack]
    println("Got Track: " + track.getCodec + " from " + mp4Container.getMovie.getTracks(AudioTrack.AudioCodec.AAC).size())

    val audioFMT = new AudioFormat(track.getSampleRate, track.getSampleSize, track.getChannelCount, true, true)

    /* local testing ... */
    /*val line = javax.sound.sampled.AudioSystem.getSourceDataLine(audioFMT)
    line.open()
    line.start()
    */

    sBuffer = new ByteOutputStream()
    println("Got Audio Format: " + audioFMT + " ")

    val decoder = new Decoder(track.getDecoderSpecificInfo)
    while( track.hasMoreFrames ) {
      val frame = track.readNextFrame()
      val buffer = new SampleBuffer()

      decoder.decodeFrame(frame.getData,buffer)
      sBuffer.write( buffer.getData )
      //line.write(buffer.getData,0,buffer.getData.length)
    }

    println("AudioBuffer: " + sBuffer.getBytes.length)
    val inStream = new ByteInputStream(sBuffer.getBytes,sBuffer.size())

    println("AudioSystem it's your time")
    //setVolume(150f)
    setAudioSource(new AudioInputStream(inStream,audioFMT,sBuffer.getBytes.length))
    playing = true

    //Commands for testing: 
    //!sound General http://localhost:8080/videoplayback.mp4
    //!sound General https://r8---sn-uxax3vh50nugp5-8px6.googlevideo.com/videoplayback?upn=bo-B526ERB4&key=yt6&pl=17&initcwndbps=956250&sparams=dur%2Cei%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&source=youtube&requiressl=yes&ratebypass=yes&mime=video%2Fmp4&ipbits=0&mn=sn-uxax3vh50nugp5-8px6&mm=31&signature=74B4C59087BFE1783F77769731027F0D03534A52.3CE1D86B1990C2E2D7358ABA78AD3D19354F22B2&itag=18&sver=3&dur=234.266&mv=m&mt=1472175222&ms=au&lmt=1458207802428817&ip=62.46.184.41&ei=rp2_V8zXO8riiQbZqoXABg&expire=1472197135&id=o-ABdG_0FYV9N9qTdLONrb_avMrmsKPSG-5sU2f5rQB-tw
  }

  override def isStopped: Boolean = !playing

  override def isStarted: Boolean = !playing

  override def restart(): Unit = ???

  override def isPlaying: Boolean = playing

  override def pause(): Unit = playing = false
}
