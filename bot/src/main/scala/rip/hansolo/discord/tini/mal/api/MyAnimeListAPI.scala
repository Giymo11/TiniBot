package rip.hansolo.discord.tini.mal.api

import com.mashape.unirest.http.Unirest
import rip.hansolo.discord.tini.mal.model.{Anime, Manga}

import scala.xml._

/**
  * Created by Reiti on 19.08.2016.
  */
class MyAnimeListAPI(username: String, password: String) {

  def findAnime(name: String): Option[List[Anime]] = {
    val animeReq = Unirest.get("http://myanimelist.net/api/anime/search.xml").basicAuth(username, password).queryString("q", name)
    println("test")
    val res = animeReq.asString
    res.getStatus match {
      case 204 => None
      case _ =>
        parseAnime(XML.loadString(res.getBody))
    }
  }

  def findManga(name: String): Option[List[Manga]] = {
    val mangaReq = Unirest.get("http://myanimelist.net/api/manga/search.xml").basicAuth(username, password).queryString("q", name)
    val res = mangaReq.asString
    res.getStatus match {
      case 204 => None
      case _ =>
        parseManga(XML.loadString(res.getBody))
    }
  }


  private[this] def isElem(n: Node): Boolean = n match {
    case e: Elem => true
    case _ => false
  }

  private[this] def parseAnime(list: Node): Option[List[Anime]] = list.head.child match {
    case _ :: Nil => None
    case _ => Some(list.head.child.filter(isElem).map(a => Anime(a)).toList)
  }

  private[this] def parseManga(list: Node): Option[List[Manga]] = list.head.child match {
    case _ :: Nil => None
    case _ => Some(list.head.child.filter(isElem).map(m => Manga(m)).toList)
  }



}
