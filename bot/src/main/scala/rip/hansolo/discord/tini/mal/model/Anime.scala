package rip.hansolo.discord.tini.mal.model

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by Reiti on 19.08.2016.
  */

case class Anime(id: Int, title: String, titleEnglish: String, score: Double, episodes: Int, synonyms: List[String],
                 showType: String, status: String, startDate: Date, endDate: Date, synopsis: String, imageUrl: String)

object Anime {
  lazy val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

  def apply(anime: scala.xml.Node): Anime = {
    val id = (anime\"id").text.toInt
    val score = (anime\"score").text.toDouble
    val episodes = (anime\"episodes").text.toInt
    val title = (anime\"title").text
    val titleEnglish = (anime\"english").text
    val synonyms = (anime\"synonyms").text.split(";").toList
    val showType = (anime\"type").text
    val status = (anime\"status").text
    val startDate = dateFormat.parse((anime\"start_date").text)
    val endDate = dateFormat.parse((anime\"end_date").text)
    val synopsis = (anime\"synopsis").text
    val imageUrl = (anime\"image").text
    new Anime(id, title, titleEnglish, score, episodes, synonyms, showType, status, startDate, endDate, synopsis, imageUrl)
  }
}
