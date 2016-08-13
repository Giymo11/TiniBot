package rip.hansolo.discord.tiniweb


import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.finagle.http.{HeaderMap, Request, Response}
import com.twitter.io.Buf
import com.twitter.util.Await

import io.finch.{body => _, head => _, _}
import io.finch._


object Main {

  // no problem because it cannot be accessed before it is set. also, will not be set more than once.
  var instance: Option[ListeningServer] = _

  val index: Endpoint[Buf] = get(/) {
    Ok (
      Buf.Utf8(
        Content.fullPage(
          title = "Awoo",
          include = Content.indexFrag)
          .render
      )
    )
  }

  val shutdown: Endpoint[Buf] = get("shutdown" :: param("password")) { (password: String) =>
    if(password == "secret") {
      for (server <- instance)
        server.close()
      Ok(Buf.Utf8("Shutdown."))
    } else NotFound(new IllegalArgumentException("dont do that m8"))
  }

  implicit val e: Encode.Aux[Exception, Text.Html] = Encode.instance((e, cs) => Buf.Utf8(e.getMessage))

  val api: Service[Request, Response] = (index :+: shutdown).toServiceAs[Text.Html]

  def main(args: Array[String]): Unit = {
    val server = Http.server
      .serve("0.0.0.0:80", api)

    instance = Some(server)

    Await.ready(server)
  }
}
