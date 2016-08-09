package rip.hansolo.discord.tini

import scala.util.Random

/**
  * Created by Giymo11 on 09.08.2016.
  */
object Util {
  def oneOf[T](xs: T*) = xs.apply(Random.nextInt(xs.size))
}
