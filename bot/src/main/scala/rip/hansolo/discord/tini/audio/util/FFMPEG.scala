package rip.hansolo.discord.tini.audio.util

import javax.sound.sampled.AudioFormat

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.dv8tion.jda.audio.AudioConnection

import scala.io.StdIn

/**
  * Created by: 
  *
  * @author Raphael
  * @version 28.08.2016
  */

object FFMPEG_TESTER {

  def main(argvs: Array[String]): Unit = {
    val audioFMT = new AudioFormat(48000, 16, 2, true, true)
    val line = javax.sound.sampled.AudioSystem.getSourceDataLine(audioFMT)


    Task.fromFuture(FFmpegMediaServer.addMediaResource("RAW_STREAM_TEST","D:/out.ts").future)
        .runAsync
        .andThen { case mediaData =>
            println("MediaInstance started ...")

            line.open()
            line.start()

            val in = mediaData.get.socket.getInputStream
            val d = new Array[Byte](AudioConnection.OPUS_FRAME_SIZE * 2)

            while (in.read(d) == AudioConnection.OPUS_FRAME_SIZE * 2) {
              //val t = System.currentTimeMillis()
              line.write(d, 0, d.length)
            }

            line.stop()
        }


    StdIn.readLine()
    FFmpegMediaServer.deleteMediaResource("RAW_STREAM_TEST")
  }

}
