package rip.hansolo.discord.tini

import cats.data.Xor

import scala.util.Random

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
}
