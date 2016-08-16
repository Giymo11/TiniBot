package rip.hansolo.discord.tini.brain

import java.io.File

import rip.hansolo.discord.tini.commands.Command

/**
  * Created by: 
  *
  * @author Raphael
  * @version 16.08.2016
  */
object CommandSearcher {

  private val sPackage = "rip.hansolo.discord.tini.commands"

  /* usage of dark java magic, don't touch it burns ... */
  def getClassNames(pkg: String): List[String] = {
    val url = Thread.currentThread
      .getContextClassLoader
      .getResource( sPackage.replace(".","/") )

    (for( file <- new File(url.getFile).listFiles() ) yield {
        file.getName.split(".class")(0)
    }).filter(!_.contains("$")).toList.distinct
  }

  /* this part contains the dark scala reflection magic, you touch you die */
  def registerAllCommands(): Unit = {
    import scala.reflect.runtime.universe

    val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
    val cls = getClassNames(sPackage)

    for( c <- cls ) {
      val module = runtimeMirror.staticModule(sPackage + "." + c)

      if( module.typeSignature.typeSymbol.isClass ) {
        val obj = runtimeMirror.reflectModule(module)
        obj.instance.asInstanceOf[Command].registerCommand() /* touch obj & load it */
      }
    }
  }
}
