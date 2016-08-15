package rip.hansolo.discord.tiniweb.content.elements


import scala.language.postfixOps

import scalacss.Defaults._
import scalacss.Macros.Color

import rip.hansolo.discord.tiniweb.generic._


/**
  * Created by Giymo11 on 15.08.2016.
  */
object IndexStyles extends StyleSheet.Inline {

  import dsl._
  import MyMaterialDesignTheme.GreyOrange
  import MyMaterialDesignTheme.MaterialFonts._
  import GenericStyles._

  val bigImg = style(
    height(100 %%),
    width(100 %%),
    objectFit.contain,
    objectPosition := "75% 100%"
  )

  val contentHeader = style(
    color(GreyOrange.a200),
    textAlign.center,
    myFont(display1),
    marginTop(0 px)
  )

  val contentPara = style(
    color.white,
    myFont(body1)
  )

  val imagePlaceholder = style(
    onDesktop (
      minWidth(33 %%)
    ),
    onLaptop (
      minWidth(25 %%)
    ),
    onSmall.&(onPhone).&(onPhablet).&(onTablet) (
      display.none
    )
  )

  val contentColumn = style(
    flex := "1",

    paddingBottom(16 px),
    minWidth(320 px),
    maxWidth(460 px),

    onSmall.&(onPhone) (
      minWidth(260 px)
    ),

    paddingLeft(32 px),
    paddingRight(32 px),


    onPhablet.&(onTablet) (
      paddingLeft(16 px),
      paddingRight(16 px)
    ),

    onSmall(
      paddingLeft(8 px),
      paddingRight(8 px)
    )
  )

  val content = style(
    paddingLeft(32 px),
    paddingRight(32 px),

    onSmall.&(onPhone) (
      paddingLeft(0 px),
      paddingRight(0 px)
    ),

    onPhablet(
      paddingLeft(16 px),
      paddingRight(16 px)
    ),

    display.flex,
    flexDirection.row,
    justifyContent.center,
    flexWrap.wrap
  )
}
