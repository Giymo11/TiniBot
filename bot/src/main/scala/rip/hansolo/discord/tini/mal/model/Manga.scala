package rip.hansolo.discord.tini.mal.model

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by Reiti on 19.08.2016.
  */
case class Manga(id: Int, title: String, titleEnglish: String, score: Double, chapters: Int, volumes: Int,
                 synonyms: List[String], mangaType: String, status: String, startDate: Date, endDate: Date, synopsis: String)

object Manga {

  lazy val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

  def apply(manga: scala.xml.Node): Manga = {
    val id = (manga\"id").text.toInt
    val title = (manga\"title").text
    val score = (manga\"score").text.toDouble
    val chapters = (manga\"chapters").text.toInt
    val volumes = (manga\"volumes").text.toInt
    val titleEnglish = (manga\"english").text
    val synonyms = (manga\"synonyms").text.split(";").toList
    val mangaType = (manga\"type").text
    val status = (manga\"status").text
    val startDate = dateFormat.parse((manga\"start_date").text)
    val endDate = dateFormat.parse((manga\"end_date").text)
    val synopsis = (manga\"synopsis").text
    new Manga(id, title, titleEnglish, score, chapters, volumes, synonyms, mangaType, status, startDate, endDate, synopsis)
  }
}