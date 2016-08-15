package rip.hansolo.discord.tiniweb.generic


import scala.language.postfixOps

import scalacss.Defaults._
import scalacss.Macros.Color


/**
  * Created by Giymo11 on 14.08.2016.
  */
object GenericStyles extends StyleSheet.Inline {

  import MyMaterialDesignTheme.GreyOrange
  import MyMaterialDesignTheme.MaterialFonts._
  import dsl._

  def myFont(fontStyle: FontStyle, color: Color = c"#FFF") = style(
    fontStyle,
    roboto
  )

  def onDesktop = media.minWidth(1441 px)
  def onLaptop = media.maxWidth(1440 px).minWidth(1025 px)
  def onTablet = media.maxWidth(1024 px).minWidth(768 px)
  def onPhablet =  media.maxWidth(767 px).minWidth(376 px)
  def onPhone = media.maxWidth(375 px).minWidth(321 px)
  def onSmall = media.maxWidth(320 px)

  val fullbleed = mixin(
    margin(0.px),
    padding(0.px),
    height(100.%%),
    width(100.%%)
  )

  val rowCenter = style(
    display.flex,
    flexDirection.row,
    justifyContent.center,
    alignItems.center
  )

  val body = style(
    fullbleed,
    backgroundColor(GreyOrange.background)
  )

  val container = style(
    width(100 %%),
    height(100 %%),
    display.flex,
    flexDirection.column,
    alignItems.center
  )
}



