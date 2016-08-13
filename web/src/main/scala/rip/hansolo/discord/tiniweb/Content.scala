package rip.hansolo.discord.tiniweb


import scala.language.postfixOps

import scala.util.Random

import scalatags.Text.{TypedTag, tags2}

import scalacss.Defaults._


/**
  * Created by Giymo11 on 12.08.2016.
  */
object Content {

  import scalatags.Text.all._
  import Util._

  def fullPage(title: String, include: Tag) = html(
    head(
      tags2.title(title),
      Styles.render[TypedTag[String]]
    ),
    body(
      Styles.body,
      include
    )
  )

  def indexFrag = div(
    Styles.body,
    Styles.rowCenter,

    div(
      Styles.container,

      div(
        id := "navbar",
        Styles.rowCenter,
        alignItems := "center",

        flex := 0,
        width := "100%",
        minHeight := "104px", // that's the image size + padding

        img(
          Styles.roundedIcon,
          src := "https://www.hansolo.rip/res/tini_L.jpg"
        ),
        a("Discord",
          href := "https://hansolo.rip",
          flex := 1),
        a("Github",
          href := "https://github.com/Giymo11/TiniBot",
          flex := 1),
        a("Credits",
          href := "credits",
          flex := 1),
        div(flex := 3)
      ),

      div(id := "content",

        Styles.rowCenter,
        height := "calc(100% - 104px)",

        img(
          Styles.bigImg,
          src := s"https://www.hansolo.rip/res/tini${Random.nextInt(3) + 1}.png"
        ),
        div(
          width := "50%",
          height := "100%",
          p("WOTOMATO")
        )
      )
    )
  )
}


object Styles extends StyleSheet.Inline {
  import dsl._

  object Color {
    val blurple = c"#7289DA"
    val white = c"#FFFFFF"
    val greyple = c"#99AAB5"
    val dark = c"#2C2F33"
    val veryDark = c"#23272A"
  }

  val fullbleed = mixin(
    margin(0.px),
    padding(0.px),
    height(100.%%),
    width(100.%%)
  )

  val body = style(
    fullbleed,
    backgroundColor(Color.dark)
  )

  val rowCenter = style(
    display.flex,
    flexDirection.row,
    justifyContent.center
  )

  val rowBetween = style(
    display.flex,
    flexDirection.row,
    justifyContent.center
  )

  val container = style(
    width(75 %%),
    height(100 %%),
    marginLeft(32 px),
    marginRight(32 px)
  )

  val bigImg = style(
    width(50 %%),
    height(100 %%),
    objectFit.contain
  )

  val roundedIcon = style(
    width(64 px),
    height(64 px),
    borderRadius(16 px),
    padding(4 px)
  )
}