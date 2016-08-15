package rip.hansolo.discord.tiniweb.content.elements


import scala.util.Random

import scalatags.Text.all._

import rip.hansolo.discord.tiniweb.Util._


/**
  * Created by Giymo11 on 15.08.2016.
  */
object IndexContent {

  def image = div(
    IndexStyles.imagePlaceholder,
    flex := "0",
    img(
      IndexStyles.bigImg,
      src := s"https://www.hansolo.rip/res/tini${Random.nextInt(3) + 1}.png"
    )
  )

  val philosophyText = Seq(
    "Bacon ipsum dolor amet beef strip steak spare ribs et exercitation. Veniam bresaola strip steak, bacon boudin sausage ribeye ham ullamco labore. Irure frankfurter in, tongue eu est pariatur shoulder aliquip pork sausage. Enim incididunt ipsum consectetur meatball ex spare ribs sint pork belly ut.",
    "Laborum pancetta magna landjaeger, laboris est adipisicing sunt pastrami excepteur pork loin officia labore. Dolore excepteur andouille, salami turducken shankle pastrami ham hock corned beef alcatra reprehenderit incididunt tail. Est porchetta ad, fatback bacon beef ribs dolor laborum landjaeger biltong fugiat officia et short loin. Veniam ham hock quis pork shank pancetta rump aliquip id kielbasa aliqua andouille cupidatat fugiat labore."
  )

  val container = div(
    IndexStyles.content,
    flex := "1",

    div(
      IndexStyles.contentColumn,
      h1(
        IndexStyles.contentHeader,
        "Philosophy"
      ),
      for(text <- philosophyText) yield p(text, IndexStyles.contentPara)
    ),
    div(
      IndexStyles.contentColumn,
      h1(
        IndexStyles.contentHeader,
        "Features"
      ),
      for(text <- philosophyText.reverse) yield p(text, IndexStyles.contentPara)
    )
  )
}
