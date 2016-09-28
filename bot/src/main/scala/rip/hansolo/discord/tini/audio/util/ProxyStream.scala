package rip.hansolo.discord.tini.audio.util

import java.net.{Socket, URLConnection}

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.execution.atomic.Atomic

import scala.concurrent.duration._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 28.09.2016
  */
class ProxyStream(media: URLConnection,proxy: Socket) {

  private val bufferSize: Int = 4096  // Bytes
  private val readDelay: Long = 2  // ms
  private val noDataLeft      = Atomic( false )

  val recvTask = Task {

    try {
      val buffer = new Array[Byte]( bufferSize )
      val rBytes = media.getInputStream.read(buffer,0,bufferSize)

      if( rBytes == 0 ) {
        destroy()
        println(s"END_OF_STREAM read $rBytes bytes ")
      } else {
        proxy.getOutputStream.write( buffer , 0 , rBytes )
      }
    } catch {
      case all: Exception =>
        all.printStackTrace()
        destroy()

        throw all
    }

  }.restartUntil( (Unit) => noDataLeft.get )
   .delayExecution( readDelay millisecond )
   .runAsync

  private val httpJunkReaderTask = Task {
    val buffer = new Array[Byte]( bufferSize )
    proxy.getInputStream.read(buffer)
    buffer.foreach(  x=>print(x.asInstanceOf[Char]) )
  }.runAsync

  def destroy(): Unit = {
    recvTask.cancel()
    httpJunkReaderTask.cancel()

    noDataLeft.set( true )

    media.getInputStream.close()
    proxy.close()
  }

}