package rip.hansolo.discord.tini.commands
import net.dv8tion.jda.entities.Message
import rip.hansolo.discord.tini.resources.TiniDriveImages

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by: 
  *
  * @author Raphael
  * @version 15.08.2016
  */
object DriveImage extends Command {

  override def prefix: String = "!image"

  /**
    *
    * @param args    The return of its unapply. It's the String needed for the execution of the command
    *                Mostly here for convenience reasons, subject to change
    * @param message The message which
    */
  override def exec(args: String, message: Message): Unit = {
    Future {
      val file = TiniDriveImages.driveImage

      if (file.isDefined) {
        message.getChannel.sendFile(file.get, null)
        file.get.delete()
      }
    }
  }
}
