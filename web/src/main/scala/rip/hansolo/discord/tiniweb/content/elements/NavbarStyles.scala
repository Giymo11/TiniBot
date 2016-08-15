package rip.hansolo.discord.tiniweb.content.elements


import scala.language.postfixOps

import scalacss.Defaults._

import rip.hansolo.discord.tiniweb.generic._


/**
  * Created by Giymo11 on 15.08.2016.
  */
object NavbarStyles extends StyleSheet.Inline{
  import dsl._
  import GenericStyles._
  import MyMaterialDesignTheme._

  val navbarPadding = 28
  val navbarSize = 72

  val navbar = style(
    display.flex,
    flexDirection.row,
    justifyContent.center,
    alignItems.center,

    flex := "0",

    margin(navbarPadding px),

    width :=! s"calc(95% - ${2*navbarPadding}px)",
    minHeight(navbarSize px) // that's the image size + padding
  )

  val roundedIcon = style(
    width(64 px),
    height(64 px),
    borderRadius(16 px),
    padding(4 px)
  )

  val tiniName = style(
    marginLeft(24 px),
    marginRight(24 px),

    color(GreyOrange.a200),

    myFont(MaterialFonts.display3)
  )

  val tiniLink = style(
    textDecoration := "none",

    backgroundColor(transparent),
    &.hover(
      backgroundColor(GreyOrange.backgroundAccented)
    )
  )

  val menuLink = style(
    onSmall.&(onPhone).&(onPhablet) (
      display.none
    ),

    backgroundColor(transparent),
    &.hover(
      backgroundColor(GreyOrange.backgroundAccented)
    ),

    padding(20 px),
    color(GreyOrange.textPrimary),
    myFont(MaterialFonts.button),
    textDecoration := "none"
  )
}
