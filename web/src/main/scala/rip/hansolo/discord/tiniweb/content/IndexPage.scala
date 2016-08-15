package rip.hansolo.discord.tiniweb.content


import scala.language.postfixOps

import scalatags.Text._

import rip.hansolo.discord.tiniweb.content.elements._
import rip.hansolo.discord.tiniweb.generic._


/**
  * Created by Giymo11 on 12.08.2016.
  */
object IndexPage {

  import scalatags.Text.all._
  import rip.hansolo.discord.tiniweb.Util._

  import scalacss.Defaults._

  def rootPage = html(
    head(
      meta(charset := "utf-8"),
      meta(httpEquiv := "X-UA-Compatible", content := "IE=edge"),
      meta(name := "viewport", content := "width=device-width, initial-scale=1"),
      meta(name := "description", content := "Tini is here! This is a fun, useful and open-source bot for Discord! Take a look!"),
      meta(name := "author", content := "Giymo11"),
      tags2.title("Tini: Bot for Discord at day, Waifu at night."),
      link(rel := "shortcut icon", href := "https://www.hansolo.rip/res/favicon.ico"),
      link(
        href := "https://fonts.googleapis.com/css?family=" +
          "Lato:" + "100,100i,300,300i,400,400i,700,700i,900,900i" + "|" +
          "Roboto:" + "100,100i,300,300i,400,400i,500,500i,700,700i,900,900i" + "|" +
          "Source+Sans+Pro:" + "200,200i,300,300i,400,400i,600,600i,700,700i,900,900i",
        rel := "stylesheet"
      ),
      MyMaterialDesignTheme.render[TypedTag[String]],
      NavbarStyles.render[TypedTag[String]],
      IndexStyles.render[TypedTag[String]],
      GenericStyles.render[TypedTag[String]]
    ),
    body(
      GenericStyles.body,
      wholeFrag
    )
  )

  def wholeFrag = div(
    GenericStyles.body,
    GenericStyles.container,

    NavbarContent.element,

    div(id := "content",
      display := "flex",
      flexDirection := "row",
      justifyContent := "center",

      height := s"calc(100% - ${NavbarStyles.navbarSize + 2*NavbarStyles.navbarPadding}px)",
      width := "100%",

      IndexContent.image,
      IndexContent.container
    )
  )
}

