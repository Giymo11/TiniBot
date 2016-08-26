package rip.hansolo.discord.tini.brain

import java.nio.file._
import java.util.Collections

import rip.hansolo.discord.tini.Util._
import rip.hansolo.discord.tini.commands.Command

import scala.collection.mutable.ListBuffer
import scala.reflect.io.File

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
    val resourceString = pkg.replace(".","/")

    val url = Thread.currentThread
      .getContextClassLoader
      .getResource( resourceString ).toURI

    val path: Path = url.getScheme match {
      case "jar" => /* use virtual file system */
        println("[CommandResolver] Use Virtual File System for .jar File Source ...")
        val fileSystem: FileSystem = FileSystems.newFileSystem( url, Collections.emptyMap().asInstanceOf[java.util.Map[String,Object]] )
        fileSystem.getPath( resourceString );
      case _ =>  Paths.get(url)
    }


    val classNames = new ListBuffer[String]()
    def buildList(p: Path): Unit = {
      val name = p.getFileName.toString

      /* check if its the pkg folder -> why is this even here? */
      if( name != pkg.substring(pkg.lastIndexOf(".")+1,pkg.length) )
        Files.isDirectory(p) match {
          case true  => classNames ++= getClassNames( pkg + "." + name.replace(".class","") )
          case false => classNames += pkg + "." + name.replace(".class","")
        }
    }

    Files.walk(path,1).forEach( (p: Path) => buildList(p) )
    classNames.filter(!_.contains("$")).toList.distinct
  }

  /* this part contains the dark scala reflection magic, you touch you die */
  private def scalaReflectionMagic(pkg: String): Unit = {
    val cls = getClassNames(pkg)

    for( c <- cls ) {
      val module = runtimeMirror.staticModule(c)

      if( module.typeSignature.typeSymbol.isClass ) {
        val obj = runtimeMirror.reflectModule(module)

        obj.instance.isInstanceOf[Command] match { /* Fixes Command object glitch */
          case true => obj.instance.asInstanceOf[Command].registerCommand() /* touch obj & load it */
          case _ => println(s"[CommandResolver] Class $c does not seem to be a instance from the Command trait => It does get ignored")
        }

      }
    }
  }

  def registerAllCommands(packages: List[String] = List[String](masterPKG)): Unit = packages.foreach(scalaReflectionMagic)
}
