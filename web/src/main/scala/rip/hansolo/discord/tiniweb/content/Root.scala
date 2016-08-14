package rip.hansolo.discord.tiniweb.content


import scala.language.postfixOps

import scala.util.Random

import scalatags.Text.{TypedTag, tags2}


/**
  * Created by Giymo11 on 12.08.2016.
  */
object Root {

  import scalatags.Text.all._
  import rip.hansolo.discord.tiniweb.Util._

  import scalacss.Defaults._

  def pageSkeletonWith(bodyFrag: Tag) = html(
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
      Styles.render[TypedTag[String]]
    ),
    body(
      Styles.body,
      bodyFrag
    )
  )

  val tiniLink = a(
    Styles.tiniLink,

    href := "https://discord.hansolo.rip",
    Styles.rowCenter,

    img(
      Styles.roundedIcon,
      src := "https://www.hansolo.rip/res/tini_L.jpg"
    ),

    span(
      Styles.tiniName(isSelected = true),
      "Tini"
    )
  )

  val menuLinks = Seq(
    ("Discord", "https://discord.gg/xXGSbrs"),
    ("Github", "https://github.com/Giymo11/TiniBot"),
    ("Credits", "/credits") // TODO !
  )

  val philosophyText = Seq(
    "Bacon ipsum dolor amet beef strip steak spare ribs et exercitation. Veniam bresaola strip steak, bacon boudin sausage ribeye ham ullamco labore. Irure frankfurter in, tongue eu est pariatur shoulder aliquip pork sausage. Enim incididunt ipsum consectetur meatball ex spare ribs sint pork belly ut.",
    "Laborum pancetta magna landjaeger, laboris est adipisicing sunt pastrami excepteur pork loin officia labore. Dolore excepteur andouille, salami turducken shankle pastrami ham hock corned beef alcatra reprehenderit incididunt tail. Est porchetta ad, fatback bacon beef ribs dolor laborum landjaeger biltong fugiat officia et short loin. Veniam ham hock quis pork shank pancetta rump aliquip id kielbasa aliqua andouille cupidatat fugiat labore."
  )

  def indexFrag = div(
    Styles.body,
    Styles.container,

    div(
      Styles.navbar,

      tiniLink,

      div(
        width := "100%",
        display := "flex",
        flexDirection := "row",
        justifyContent := "center",
        alignItems := "baseline",

        div(
          id := "spacer",
          flex := 1
        ),

        for(menuLink <- menuLinks) yield a(
          Styles.menuLink,
          menuLink._1,
          href := menuLink._2,
          flex := 0
        )
      )
    ),

    div(id := "content",
      display := "flex",
      flexDirection := "row",
      justifyContent := "center",

      height := s"calc(100% - ${Styles.navbarSize + 2*Styles.navbarPadding}px)",
      width := "100%",

      div(
        width := "33%",
        img(
          Styles.bigImg,
          src := s"https://www.hansolo.rip/res/tini${Random.nextInt(3) + 1}.png"
        )
      ),
      div(
        width := "67%",
        //height := "100%",
        display := "flex",
        flexDirection := "row",
        //justifyContent := "center",
        div(
          Styles.contentColumn,
          h1(
            Styles.contentHeader,
            "Philosophy"
          ),
          for(text <- philosophyText) yield p(text, Styles.contentPara)
        ),
        div(
          Styles.contentColumn,
          h1(
            Styles.contentHeader,
            "Features"
          ),
          for(text <- philosophyText.reverse) yield p(text, Styles.contentPara)
        ),
        div(
          id := "margin",
          width := "64px"
        )
      )
    )
  )
}

