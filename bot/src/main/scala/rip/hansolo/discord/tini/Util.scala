package rip.hansolo.discord.tini


import scala.util.Random

import cats.data.Xor

import com.google.firebase.database.DatabaseReference.CompletionListener
import com.google.firebase.database.{DatabaseError, DatabaseReference}

import scala.language.implicitConversions


/**
  * Created by Giymo11 on 09.08.2016.
  */
object Util {

  def oneOf[T](xs: T*) = xs.apply(Random.nextInt(xs.size))

  /**
    * Extracts an Int out of a String
    */
  object StringInt {
    def unapply(arg: String): Option[Int] = Xor.catchNonFatal(arg.toInt).toOption
  }

  implicit def funcToDatabaseListener(func: (DatabaseError, DatabaseReference) => Unit): CompletionListener =
    new CompletionListener {
      override def onComplete(dbError: DatabaseError, dbRef: DatabaseReference): Unit = func(dbError, dbRef)
    }

  implicit def functionToConsumer[T](func: T => Unit): java.util.function.Consumer[T] =
    scala.compat.java8.FunctionConverters.asJavaConsumer(func)

  implicit def functionToPredicate[T](func: T => Boolean): java.util.function.Predicate[T] =
    scala.compat.java8.FunctionConverters.asJavaPredicate(func)

  val isWhitespace = (char: Char) => char == ' ' || System.lineSeparator().contains(char) || char == '\n'

  def isEnvSet(name: String): Boolean = System.getenv(name) != null || !System.getenv(name).isEmpty
}
