package rip.hansolo.discord.tiniweb.content


import scala.language.postfixOps
import scalacss.{Attrs, _}
import scalacss.Defaults._

/**
  * Created by Giymo11 on 14.08.2016.
  */

object Styles extends StyleSheet.Inline {


  import dsl._

  object DiscordColors {
    val blurple = c"#7289DA"
    val white = c"#FFFFFF"
    val greyple = c"#99AAB5"
    val dark = c"#2C2F33"
    val veryDark = c"#23272A"
  }

  object MyColors {

    val background = c"#212121"
    val backgroundAccented = c"#424242"

    /*
    val m100 = c"#FFCDD2"
    val m300 = c"#E57373"
    val m500 = c"#F44336"
    val m700 = c"#D32F2F"
    val m900 = c"#B71C1C"
    */ // Red

    /*
    val a100 = c"#F4FF81"
    val a200 = c"#EEFF41"
    val a400 = c"#C6FF00"
    val a700 = c"#AEEA00"
    */ // Lime

    val a100 = c"#FF9E80"
    val a200 = c"#FF6E40"
    val a400 = c"#FF3D00"
    val a700 = c"#DD2C00"

    val textPrimary = c"#FFF"
    val textSecondary = c"#DDD"
  }

  val lato = mixin(fontFamily :=! "'Lato', sans-serif")
  val sourceSansPro = mixin(fontFamily :=! "'Source Sans Pro', sans-serif")
  val roboto = mixin(fontFamily :=! "'Roboto', sans-serif")

  val fonts = Seq(lato, sourceSansPro, roboto)
    .map(
      mixin =>
        style(
          mixin,
          color(MyColors.textPrimary),
          fontSize.apply(28 px)
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
    backgroundColor(MyColors.background)
  )

  val rowCenter = style(
    display.flex,
    flexDirection.row,
    justifyContent.center,
    alignItems.center
  )

  val container = style(
    width(100 %%),
    height(100 %%),
    display.flex,
    flexDirection.column,
    //justifyContent.spaceBetween,
    alignItems.center
  )

  val navbarPadding = 32
  val navbarSize = 72

  val navbar = style(

    display.flex,
    flexDirection.row,
    justifyContent.center,
    alignItems.center,

    flex := "0",

    margin(navbarPadding px),

    width := s"calc(95% - ${2*navbarPadding}px)",
    minHeight(navbarSize px) // that's the image size + padding
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

  def tiniName(isSelected: Boolean) = style(
    marginLeft(24 px),
    marginRight(24 px),

    color(if(isSelected) MyColors.a200 else MyColors.textPrimary),

    fontSize(56 px),
    fontWeight := "500",

    sourceSansPro
  )

  val tiniLink = style(
    textDecoration := "none",

    backgroundColor(transparent),
    &.hover(
      backgroundColor(MyColors.backgroundAccented)
    )
  )

  val menuLink = style(

    backgroundColor(transparent),
    &.hover(
      backgroundColor(MyColors.backgroundAccented)
    ),

    padding(20 px),
    color(MyColors.textPrimary),
    fontSize.apply(26 px),
    fontWeight := "600",
    sourceSansPro,
    textDecoration := "none"
  )

  val contentColumn = style(
    flex := "1",
    paddingLeft(16 px),
    paddingRight(16 px),
    paddingBottom(16 px)
    //width(50 %%)
  )

  val contentHeader = style(
    sourceSansPro,
    color(MyColors.a200),
    textAlign.center,
    fontSize(48 px),
    fontWeight := "500",
    marginTop(0 px)
  )

  val contentPara = style(
    sourceSansPro,
    color.white,
    fontSize(24 px),
    fontWeight := "400"
  )
}
