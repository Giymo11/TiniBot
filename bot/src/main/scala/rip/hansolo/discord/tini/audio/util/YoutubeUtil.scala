package rip.hansolo.discord.tini.audio.util

import java.io.File

import rip.hansolo.discord.tini.resources.Reference

import scala.io.Source

/**
  * Object communicates with the youtube-dl binary to get the URL of the
  * Youtube Video
  *
  * @author Raphael
  * @version 26.08.2016
  */
object YoutubeUtil {

  def getDownloadURL(url: String): Option[String] = {
    val binary = new ProcessBuilder(new File(Reference.youtubedlBinary).getAbsolutePath,
                                    "-f","18","--skip-download","--no-check-certificate","-g",url)

    val process = binary.start()
    val output = Source.fromInputStream(process.getInputStream).getLines().mkString(" ")
    val error = Source.fromInputStream(process.getErrorStream).getLines().mkString(" ")

    process.waitFor() match {
      case 0 => Some(output)
      case _ => println("Error while converting: " + error); None
    }
  }

}
