package rip.hansolo.discord.tini.audio.util

import java.net.{Socket, URLConnection}

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.execution.atomic.Atomic

import scala.concurrent.Promise
import scala.concurrent.duration._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 28.09.2016
  */
class ProxyStream(media: URLConnection,proxy: Socket,fatalErrorPromise: Promise[Unit]) {


  private val bufferSize: Int = 4096  // Bytes
  private val readDelay: Long = 10    // ms
  private val noDataLeft      = Atomic( false )
  private val buffer: Array[Byte] = new Array[Byte]( bufferSize )

  val recvTask = Task { tryReadConnection() }
                      .restartUntil( (Unit) => noDataLeft.get )
                      .delayExecution( readDelay millisecond )
                      .runAsync

  def tryReadConnection(): Boolean = {
    try {
      val rBytes = media.getInputStream.read(buffer,0,bufferSize)

      if( rBytes <= 0 ) destroy()
      else proxy.getOutputStream.write( buffer , 0 , rBytes )

      true
    } catch {
      case all: Exception =>
        System.err.println("There was a error!")
        //all.printStackTrace()
        destroy()

        System.err.println("There was a error! (2)")
        fatalErrorPromise.failure( all )
        fatalErrorPromise.success()

        System.err.println("There was a error! (3)")
        false
    }
  }

  /*
  private val httpJunkReaderTask = Task {
    val buffer = new Array[Byte]( bufferSize )
    proxy.getInputStream.read(buffer)
    buffer.foreach(  x=>print(x.asInstanceOf[Char]) )
  }.runAsync
  */

  def destroy(): Unit = {
    printf("Called ProxyStream.destroy()")
    try {
      recvTask.cancel()
      //httpJunkReaderTask.cancel()

      noDataLeft.set(true)

      media.getInputStream.close()
      proxy.close()
    } catch { case _: Exception => /* do nothing */ }
  }

}