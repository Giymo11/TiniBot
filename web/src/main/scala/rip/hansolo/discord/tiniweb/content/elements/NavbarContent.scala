package rip.hansolo.discord.tiniweb.content.elements


import scala.language.postfixOps

import rip.hansolo.discord.tiniweb.Util._
import rip.hansolo.discord.tiniweb.generic.GenericStyles


/**
  * Created by Giymo11 on 15.08.2016.
  */
object NavbarContent {

  import scalatags.Text.all._

  val tiniLink = a(
    NavbarStyles.tiniLink,

    href := "https://discord.hansolo.rip",
    GenericStyles.rowCenter,

    img(
      NavbarStyles.roundedIcon,
      src := "https://www.hansolo.rip/res/tini_L.jpg"
    ),

    span(
      NavbarStyles.tiniName,
      "Tini"
    )
  )

  val menuItems = {
    val menuLinks = Seq(
      ("Discord", "https://discord.gg/xXGSbrs"),
      ("Github", "https://github.com/Giymo11/TiniBot"),
      ("Credits", "/credits") // TODO !
    )

    for(menuLink <- menuLinks) yield a(
      NavbarStyles.menuLink,
      menuLink._1,
      href := menuLink._2,
      flex := 0
    )
  }

  val element = div(
    NavbarStyles.navbar,

    tiniLink,

    div(
      width := "100%",
      GenericStyles.rowCenter,

      div(
        id := "spacer",
        flex := 1
      ),

      menuItems
    )
  )
}

