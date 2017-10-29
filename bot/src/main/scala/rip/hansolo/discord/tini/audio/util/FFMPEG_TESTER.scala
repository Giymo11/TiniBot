package rip.hansolo.discord.tini.audio.util

import javax.sound.sampled.AudioFormat

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.core.audio.AudioConnection

import scala.io.StdIn

/**
  * Created by: 
  *
  * @author Raphael
  * @version 28.08.2016
  */

object FFMPEG_TESTER {

  val video = ""
  def main(argvs: Array[String]): Unit = {
    val audioFMT = new AudioFormat(48000, 16, 2, true, true)
    val line = javax.sound.sampled.AudioSystem.getSourceDataLine(audioFMT)
try {

  Task.fromFuture(FFmpegMediaServer.addMediaResource("RAW_STREAM_TEST", video, useProxy = true).future)
    .runAsync
    .andThen { case mediaData =>
      println("MediaInstance started ...")

      line.open()
      line.start()

      val in = mediaData.get.socket.getInputStream
      val d = new Array[Byte](AudioConnection.OPUS_FRAME_SIZE * 2)


      while (in.available() < AudioConnection.OPUS_FRAME_SIZE * 2) {
        Thread.sleep(1000)
        println("DATA_AVAILABLE: " + mediaData.get.socket.getInputStream.available())
      }

      try {
        while (true) {
          val read = in.read(d)
          line.write(d, 0, read)
        }
      } catch {
        case all: Exception => all.printStackTrace()
      }
      line.stop()
      println("MediaInstance seems to be finished ...")
      println("Process Alive: " + mediaData.get.process.isAlive)
    }


  StdIn.readLine()
  FFmpegMediaServer.deleteMediaResource("RAW_STREAM_TEST")
} catch {
  case all: Exception => all.printStackTrace()
}

  }

}
