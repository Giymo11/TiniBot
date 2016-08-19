package rip.hansolo.discord.tini.mal.api

import com.mashape.unirest.http.Unirest
import rip.hansolo.discord.tini.mal.model.Anime

import scala.xml._

/**
  * Created by Reiti on 19.08.2016.
  */
class MyAnimeListAPI(username: String, password: String) {
  lazy val searchReq = Unirest.get("http://myanimelist.net/api/{type}/search.xml").basicAuth(username, password)

  def findAnime(name: String): Option[List[Anime]] = {
    val animeReq = searchReq.routeParam("type", "anime").queryString("q", name)
    val res = animeReq.asString
    res.getStatus match {
      case 204 => None
      case _ =>
        parseAnime(XML.loadString(res.getBody))
    }

  }

  def isElem(n: Node): Boolean = n match {
    case e: Elem => true
    case _ => false
  }

  private def parseAnime(list: Node): Option[List[Anime]] = list.head.child match {
    case _ :: Nil => None
    case _ => Some(list.head.child.filter(isElem).map(a => Anime(a)).toList)
  }



}
