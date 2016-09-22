package rip.hansolo.discord.tini.audio.util

import java.io._
import java.net.{ServerSocket, Socket}
import java.nio.ByteBuffer
import java.util.concurrent.{ConcurrentLinkedQueue, TimeUnit}
import javax.sound.sampled.{AudioFormat, AudioSystem}

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.execution.atomic.Atomic
import net.dv8tion.jda.audio.AudioConnection
import rip.hansolo.discord.tini.audio.util.mpegts.MTSPacket
import rip.hansolo.discord.tini.resources.Reference

import scala.concurrent.{Future, Promise}
import scala.io.{Source, StdIn}
import scala.util.Random
import scala.collection.JavaConverters._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 28.08.2016
  */


abstract class FFMPEG(port: Int,out: ConcurrentLinkedQueue[Array[Byte]],notify: Task[Unit]) {

  private val MPEG_TS_PACKET_LEN = 188 // mpegts packet size //AudioConnection.OPUS_FRAME_SIZE * 4 + FFMPEG.offset.get
  private val AUDIO_FRAME_LEN    = 96 * 20

  protected val contentServer     = new ServerSocket(port)
  protected var ffmpegConnection: Socket = _
  protected val ffmpegProcess: Process   = spawnProcess()

  val streamReady = Promise[Unit]
  val bufferEmpty = Atomic(true)

  private val conAckHandler       = Task { ffmpegConnection = this.contentServer.accept() }.runAsync
  protected val contentFeeder     = Task {
    val payloadBuffer = new scala.collection.mutable.Queue[Byte]

    var running = true
    while( running ){
      var opusPacket = new Array[Byte](0)

      /* drain old data into opus package*/
      if( payloadBuffer.nonEmpty ) {
        val oPB = new Array[Byte]( if ( payloadBuffer.size < AUDIO_FRAME_LEN ) payloadBuffer.size else AUDIO_FRAME_LEN )
        for( i <- oPB.indices )
          oPB.update(i,payloadBuffer.dequeue)

        opusPacket ++= oPB
      }

      //System.err.println("Inital OpusLen: " + opusPacket.length)
      //System.err.println("Payload Backlog: " + payloadBuffer.length)

      /* discord needs fixed sized packet */
      while( opusPacket.length < AUDIO_FRAME_LEN ) {
        /* buffer of mpeg-ts packet */
        val rawPacket = new Array[Byte]( MPEG_TS_PACKET_LEN )
        //System.err.println("Starting to read FFMPEG stdout")

        /* read packet -> EOF is corrupted  one, don't play that on discord */
        if( ffmpegProcess.getInputStream.read(rawPacket) != MPEG_TS_PACKET_LEN ) running = false
        else {
          //System.err.println("ByteBuffer.wrap(_)")
          /* parse packet and set load */
          val mts1 = new MTSPacket(ByteBuffer.wrap(rawPacket))
          //System.err.println("Magic: " + mts1.magic.asInstanceOf[Char])
          //System.err.println("Payload len: " + mts1.payload.length)

          if( mts1.payloadPresent )
            opusPacket ++= mts1.payload
        }

        //System.err.println("Current OpusPacket len: " + opusPacket.length)
        //System.err.println("Run Status: " + running)
      }

      //System.err.println("Ensure Opus len 960")
      /* ensure opus packet len and drain above 960 byte into old buffer */
      if( opusPacket.length > AUDIO_FRAME_LEN ) {
        for( i <- AUDIO_FRAME_LEN until opusPacket.length ) payloadBuffer.enqueue( opusPacket(i) )
        //System.err.println("Done, dropRight of 960")
        opusPacket = opusPacket.dropRight(opusPacket.length - AUDIO_FRAME_LEN)
      }

      //System.err.println("Current opus len: " + opusPacket.length)

      if( !ffmpegProcess.isAlive ) running = false
      else out.add(opusPacket) //drop header

      if( bufferEmpty.get ) {
        bufferEmpty.lazySet(false)
        notify.runAsync
      }
    }

    println("finished")
    ffmpegProcess.destroy()
  }
  var feederTask = contentFeeder.runAsync

  val ctl = Task {
    while( true ) {
      FFMPEG.offset.synchronized { FFMPEG.offset.wait() }
      out.clear()
    }
  }

  private val stdErrReader = Task {
    while( ffmpegProcess.isAlive )
      Source.fromInputStream(ffmpegProcess.getErrorStream).foreach( print(_) )

    System.err.println("STDERR: Eof ? ")
  }.runAsync
  private val stdOutReader = Task {
    while( ffmpegProcess.isAlive )
      Source.fromInputStream(ffmpegProcess.getInputStream).foreach( System.err.print )

    System.err.println("STDOUT: Eof ? ")
  }.runAsync

  def close(): Unit = {
    if( ffmpegConnection != null ) ffmpegConnection.close()
    if( ffmpegProcess != null ) {
      ffmpegProcess.waitFor(1, TimeUnit.SECONDS)
      ffmpegProcess.destroy()
    }
  }

  def spawnProcess(): Process
}

/*
class YoutubeFfmpegStream(port: Int,in: ChunkedMpegTsStream,out: ConcurrentLinkedQueue[Array[Byte]],notify: Task[Unit]) extends FFMPEG(port = port,out = out,notify = notify){

  in.contentFlipped.future.onSuccess { case x =>
    while( in.available() > 0 )
      ffmpegConnection.getOutputStream.write(in.read())
  }

  def spawnProcess(): Process = {
    new ProcessBuilder(Reference.ffmpegBinary,"-i",s"tcp://127.0.0.1:$port","-acodec","opus","-ar","48000","-vn","-b:a","45k","-f","mpegts","-").start()
  }
}
*/

class WebRadioFfmpegStream(internalServerPort: Int,url: String,out: ConcurrentLinkedQueue[Array[Byte]], notify: Task[Unit]) extends FFMPEG(port=internalServerPort,out = out,notify = notify) {
  def spawnProcess(): Process = {
    contentServer.close()
    println("Spawn process")
    val pb = new ProcessBuilder(Reference.ffmpegBinary,"-i",url,"-acodec","opus","-vn","-ar","48000","-b:a","96k","-f","mpegts","-loglevel","verbose","-nostdin","-stats","-hide_banner","-vbr","off","-y","-")
    val env = pb.environment()
    env.put("AV_LOG_FORCE_NOCOLOR","AV_LOG_FORCE_NOCOLOR")
    println(pb.command().asScala.mkString(" "))

    pb.start()
  }
}

object FFMPEG {
  var offset = Atomic(100)
  val housetime = "http://listen.housetime.fm/tunein-aacisdn-pls"
  val input = "D:\\Users\\Raphael\\Downloads\\seq_2.ts"

  def main(args: Array[String]): Unit = {
    val serverPort = new Random().nextInt(40000)+20000
    val audioData = new ConcurrentLinkedQueue[Array[Byte]]()

    val pipedOutputStream = new PipedOutputStream()
    val pipedInputStream = new PipedInputStream(pipedOutputStream)


    println("create WebRadioFFmpegStream")
    var webradio: WebRadioFfmpegStream = null
    webradio = new WebRadioFfmpegStream(serverPort,housetime,audioData,Task {
                      println("Audio Stream got ready ... " + audioData.size())
                      val data = audioData.peek()


                         while (!audioData.isEmpty) {
                           println("read")
                           pipedOutputStream.write(audioData.poll())
                         }

                         webradio.bufferEmpty.set(true)


      println("finished reading")
                    })




    StdIn.readLine()
    pipedOutputStream.close()
    println("AUDIO SYSTEM: ")
    val stream = AudioSystem.getAudioInputStream(new File("./out"))
    println("format:" + stream.getFormat )

    //webradio.startFFMPEG()

    println("Press any key to halt stream")
    StdIn.readLine()
    webradio.close()
  }

}
