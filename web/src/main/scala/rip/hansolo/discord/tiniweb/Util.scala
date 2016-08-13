package rip.hansolo.discord.tiniweb


import scala.language.implicitConversions

import scalacss.{Renderer, ScalatagsTextRenderer, StyleA}

import scalatags.Text.TypedTag
import scalatags.Text.all._
import scalatags.text.Builder
import scalatags.{Escaping, text}

/**
  * Created by Giymo11 on 13.08.2016.
  */
object Util {

  case class ClassValueSource(v: String) extends Builder.ValueSource {
    override def appendAttrValue(strb: StringBuilder): Unit = {
      strb.append(' ')
      Escaping.escape(v, strb)
    }
  }

  implicit def style2Mod(stylish: StyleA): Modifier = new Modifier {
    def applyTo(t: text.Builder) = t.appendAttr("class", ClassValueSource(stylish.htmlClass))
  }

  implicit final def styleTextTagRenderer(implicit s: Renderer[String]): Renderer[TypedTag[String]] =
    new ScalatagsTextRenderer(s)
}

