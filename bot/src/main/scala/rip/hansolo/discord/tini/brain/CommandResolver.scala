package rip.hansolo.discord.tini.brain

import java.io.File

import rip.hansolo.discord.tini.commands.Command

import scala.collection.mutable.ListBuffer

/**
  * Created by: 
  *
  * @author Raphael
  * @version 16.08.2016
  */
object CommandResolver {
  import scala.reflect.runtime.universe

  private val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
  val masterPKG = "rip.hansolo.discord.tini.commands"

  /* usage of dark java magic, don't touch it burns ... */
  private def getClassNames(pkg: String): List[String] = {
    val url = Thread.currentThread
      .getContextClassLoader
      .getResource( pkg.replace(".","/") )

    val classNames = new ListBuffer[String]()
    for( resource <- new File(url.getFile).listFiles ) {
      if( resource.isDirectory ) classNames ++= getClassNames( pkg + "." + resource.getName.replace(".class","") )
      else classNames += pkg + "." + resource.getName.replace(".class","")
    }

    classNames.filter(!_.contains("$")).toList.distinct
  }

  /* this part contains the dark scala reflection magic, you touch you die */
  private def scalaReflectionMagic(pkg: String): Unit = {
    val cls = getClassNames(pkg)

    for( c <- cls ) {
      val module = runtimeMirror.staticModule(c)

      if( module.typeSignature.typeSymbol.isClass ) {
        val obj = runtimeMirror.reflectModule(module)
        obj.instance.asInstanceOf[Command].registerCommand() /* touch obj & load it */
      }
    }
  }

  def registerAllCommands(packages: List[String] = List[String](masterPKG)): Unit = packages.foreach(scalaReflectionMagic)
}
