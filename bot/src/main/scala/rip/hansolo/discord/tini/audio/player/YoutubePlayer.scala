package rip.hansolo.discord.tini.audio.player

import java.io.{BufferedInputStream, IOException}
import java.net.URL
import javax.sound.sampled.AudioFormat

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.entities.Guild
import net.sourceforge.jaad.aac.{Decoder, SampleBuffer}
import net.sourceforge.jaad.mp4.MP4Container
import net.sourceforge.jaad.mp4.api.AudioTrack

import scala.concurrent.Promise

/**
  * Created by: 
  *
  * @author Raphael
  * @version 26.08.2016
  */
class YoutubePlayer(guild: Guild) extends BasicPlayer( guild = guild ) {
  private var duration = 0.0
  private var loaded = false

  override def play(resource: String): Unit = {
    loaded match {
      case true => this.play()
      case false => this.load(resource); this.play()
    }
  }

  override def load(resource: String): Promise[Unit] = {
    val state = Promise[Unit]

    Task {
      try {
        val conn = getJDAConnection(new URL(resource))
        val bufferedStream = new BufferedInputStream(conn.getInputStream)

        //none MP4 Files -> Bot is dead!
        println("Loading stream ...")
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

        while (track.hasMoreFrames) {
          val frame = track.readNextFrame()
          val buffer = new SampleBuffer()

          decoder.decodeFrame(frame.getData, buffer)
          sBuffer.write(buffer.getData)
          //line.write(buffer.getData,0,buffer.getData.length) //<-- this will block everything
        }

        println("Stream loaded and converted")

        loaded = true
        sBuffer.close()
        load(sBuffer, audioFMT)
        state.success()
      } catch {
        case ex: IOException =>
          state.failure(ex)
      }
    }.runAsync

    state
  }



  override def getDuration: Double = this.duration
}
