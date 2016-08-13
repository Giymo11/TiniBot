package rip.hansolo.discord.tiniweb


import com.sun.javafx.css.FontFace

import scala.language.postfixOps
import scala.util.Random
import scalatags.Text.{TypedTag, tags2}
import scalacss.Defaults._
import scalacss.FontFace


/**
  * Created by Giymo11 on 12.08.2016.
  */
object Content {

  import scalatags.Text.all._
  import Util._

  def fullPage(include: Tag) = html(
    head(
      meta(charset := "utf-8"),
      meta(httpEquiv := "X-UA-Compatible", content := "IE=edge"),
      meta(name := "viewport", content := "width=device-width, initial-scale=1"),
      meta(name := "description", content := "Tini is here! This is a fun, useful and open-source bot for Discord! Take a look!"),
      meta(name := "author", content := "Giymo11"),
      tags2.title("Tini: Bot for Discord at day, Waifu at night."),
      link(
        href := "https://fonts.googleapis.com/css?family=Lato|Open+Sans|PT+Sans|Roboto|Source+Sans+Pro|Work+Sans",
        rel := "stylesheet"
      ),
      Styles.render[TypedTag[String]]
    ),
    body(
      Styles.body,
      include
    )
  )

  val menuLinks = Seq(
    ("Discord", "https://discord.hansolo.rip"),
    ("Github", "https://github.com/Giymo11/TiniBot"),
    ("Credits", "credits")
  )

  def indexFrag = div(
    Styles.body,
    Styles.container,

    div(
      Styles.navbar,

      img(
        Styles.roundedIcon,
        src := "https://www.hansolo.rip/res/tini_L.jpg"
      ),
      for(menuLink <- menuLinks) yield a(
        Styles.menuLink,
        menuLink._1,
        href := menuLink._2,
        flex := 1
      ),
      div(flex := menuLinks.size)
    ),

    div(id := "content",

      Styles.rowCenter,
      height := "calc(100% - 104px)",
      width := "100%",

      div(
        width := "50%",
        img(
          Styles.bigImg,
          src := s"https://www.hansolo.rip/res/tini${Random.nextInt(3) + 1}.png"
        )
      ),
      div(
        width := "50%",
        height := "100%",
        div(
          for(stylishFont <- Styles.fonts) yield p(
              stylishFont,
              "The quick brown fox, jumps over the lazy dog. Yes?!"
            )
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

  val roboto = mixin(fontFamily :=! "'Roboto', sans-serif")
  val openSans = mixin(fontFamily :=! "'Open Sans', sans-serif")
  val lato = mixin(fontFamily :=! "'Lato', sans-serif")
  val sourceSansPro = mixin(fontFamily :=! "'Source Sans Pro', sans-serif")
  val workSans = mixin(fontFamily :=! "'Work Sans', sans-serif")
  val ptSans = mixin(fontFamily :=! "'PT Sans', sans-serif")

  val fontMixin = mixin(
    color.white,
    fontSize.apply(28 px)
  )

  val fonts = Seq(roboto, openSans, lato, sourceSansPro, workSans, ptSans)
    .map(
      mixin =>
      style(
        mixin,
        fontMixin
      )
    )

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
    width(100 %%),
    height(100 %%),
    display.flex,
    flexDirection.column,
    justifyContent.spaceBetween,
    alignItems.center
  )

  val navbar = style(
    Styles.rowCenter,
    alignItems.center,

    flex := "0",

    width(75 %%),
    minHeight(104 px), // that's the image size + padding

    marginLeft(32 px),
    marginRight(32 px)
  )

  val bigImg = style(
    height(100 %%),
    width(100 %%),
    objectFit.contain,
    objectPosition := "75% 100%"
  )

  val roundedIcon = style(
    width(64 px),
    height(64 px),
    borderRadius(16 px),
    padding(4 px)
  )

  val menuLink = style(
    marginLeft(16 px),
    fontMixin,
    workSans,
    textDecoration := "none"

  )
}