package rip.hansolo.discord.tini.audio.util

import java.io.File
import java.net.URLDecoder
import javax.script.ScriptEngineManager

import cats.data.Xor
import org.jsoup.Jsoup

import scala.io.Source

/**
  *
  * @author Raphael
  * @version 26.08.2016
  */
object YoutubeUtil {

  /* creating script engine may take more time than .eval */
  lazy val scriptEngine      = new ScriptEngineManager().getEngineByExtension("js")

  /* some static url*/
  val YOUTUBE_URI       = "https://youtube.com"
  val YT_GOOGLE_API_URI = "https://youtube.googleapis.com/v/"

  /* Chrome 52 */
  val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36"


  def extractYoutubeIDFromUrl(url: String): String = {
    if( url.contains("youtu.be") ) url.substring(url.lastIndexOf('/')+1)
    else url.split("v=")(1)
  }

  def getDownloadURL(url: String): Option[String] = {
    val videoID = extractYoutubeIDFromUrl(url)

    val doc    = Jsoup.connect( YOUTUBE_URI + "/watch" ).data("v", videoID ).get()
    val baseJS = Jsoup.connect( "https:"+doc.select("script[name='player/base']").attr("src") )
                      .ignoreContentType(true)
                      .execute()
                      .body().replace("\n"," ")

    val vidInfo   = readVideoInfo(videoID).get
    val streamMap = getVideoStreamMap( vidInfo , baseJS )
    val content   = compileContentMap(streamMap)

    content.foreach( x => println( x._1 + " -> " + URLDecoder.decode(x._2("type"),"UTF-8")  ))

    /* mp4 coder does only support 18, 22, 140 */
    val iTag18 = content.get(18)
    val iTag22 = content.get(22)
    val iTag140 = content.get(140)

    if( iTag18.isEmpty && iTag22.isEmpty && iTag140.isEmpty ) return None
    Some(URLDecoder.decode(iTag18.getOrElse(iTag22.getOrElse(iTag140.get))("url"),"UTF-8"))
  }

  /* from JDownloader starts from here: */
  def descrableSignature(signature: String,baseJS: String): String = {
    //TODO: from youtube -> in head <script src="???" name="player/base">
    val baseJS       = Source.fromFile(new File("D:/base.js")).getLines().mkString(" ")

    /* regex magic everything will crash if one thing is not found! */
    val DescrablerRegex = """set\(\"signature\",([\$\w]+)\([\w]+\)""".r
    val desc: Option[String] = for { DescrablerRegex(g1) <- DescrablerRegex findFirstIn baseJS } yield g1

    val FunctionRegex = (desc.get +"""=function\(([^)]+)\)\{(.+?return.*?)\}""").r
    val des: Option[String] = for { FunctionRegex(g1,g2) <- FunctionRegex findFirstIn baseJS } yield g2

    val AllRegex = ("""(""" + desc.get + """=function\(([^)]+)\)\{(.+?return.*?)\}.*?)""").r
    val all: Option[String] = for { AllRegex(g1,g2,g3) <- AllRegex findFirstIn baseJS } yield g1

    val RegObjNameRegex = """([\w\d\$]+)\.([\w\d]{2})\(""".r
    val reqObjName: Option[String] = for { RegObjNameRegex(g1,g2) <- RegObjNameRegex findFirstIn des.get } yield g1

    val ReqObjRegex = ("""(var """+reqObjName.get+"""=\{[\s\S]+?\}\};)""").r
    val reqObj = for { ReqObjRegex(g1) <- ReqObjRegex findFirstIn baseJS } yield g1

    val scriptToEval = (all.get + ";" + reqObj.get + " " + desc.get + """("""" + signature + """")""").replace("};","};\n")

    scriptEngine.eval(scriptToEval).toString
  }

  /**
    * Parses the /get_vido_info json and puts it into the Map
    *
    * @param videoID Youtube Video ID
    * @return
    */
  def readVideoInfo(videoID: String): Option[Map[String,String]] = {
    val info = Jsoup.connect(YOUTUBE_URI + "/get_video_info")
      .data("video_id",videoID)
      .data("eurl", YT_GOOGLE_API_URI + videoID)
      .data("sts","16511")
      .header("User-Agent",USER_AGENT)
      .ignoreContentType(true)
      .execute()

    info.statusCode() match {
      case 200 => Some(getURLParameter(info.body(),headless = true))
      case _   =>  None
    }
  }

  /**
    * Patches signatures in the urls
    *
    * @param vidInfo Youtube Video ID
    * @param baseJS String which contains the base.js script from the Youtube Page
    * @return
    */
  def getVideoStreamMap(vidInfo: Map[String,String], baseJS: String): Map[String,String] = {
    val SignatureRegex = """/s/(.*?)/""".r
    val witheList = List("adaptive_fmts","url_encoded_fmt_stream_map","dashmpd","hlsvp")

    //TODO: use one time filter only
    val srcVidInfo = vidInfo.filterKeys( witheList.contains(_) )
    var newVidInfo = Map.empty[String,String]

    srcVidInfo.foreach( x => {
      SignatureRegex findFirstIn x._2 match {
        case Some(sig) => newVidInfo += (x._1 -> ( x._2.replace("/s/(.*?)/", "/signature/" + descrableSignature(sig,baseJS) + "/")) )
        case None => newVidInfo += (x._1 -> ( x._2))
      }
    })

    newVidInfo
  }

  def getURLParameter(url: String, headless: Boolean = false): Map[String,String] = {
    var param = Map.empty[String,String]

    headless match {
      case true  => url.split("&").foreach( s => param += ( s.split("=")(0) -> Xor.catchNonFatal(s.split("=")(1)).getOrElse("") ) )
      case false => url.split("\\?")(1).split("&").foreach( s => param += ( s.split("=")(0) -> s.split("=")(1) ) )
    }

    param
  }

  def decodeFMTUrl(url: String): Map[Int,Map[String,String]] = {
    var dataBlocks    = Map.empty[Int,Map[String,String]]
    var dataBlock = Map.empty[String,String]
    val urlParamPairs = url.replace(",","&").split("&")

    for( pp <- urlParamPairs ) {
      val key = pp.split("=")(0)
      val value = pp.split("=")(1)

      println(pp)

      if( dataBlock.contains(key) ) {
        dataBlocks += (dataBlock("itag").toInt -> dataBlock)
        dataBlock = Map.empty[String,String]
      }

      dataBlock += (key -> value)
    }

    dataBlocks += (dataBlock("itag").toInt -> dataBlock)
    dataBlocks
  }

  def compileContentMap(streamMap: Map[String,String]): Map[Int,Map[String,String]] = {
    var content = Map.empty[Int,Map[String,String]]

    streamMap.foreach(x => {
      if( x._1.contains("fmt") ) {
        content ++= decodeFMTUrl(URLDecoder.decode(x._2, "UTF-8"))
      }
    })

    content
  }

  def main(args: Array[String]): Unit = {
    println( getDownloadURL("https://www.youtube.com/watch?v=_MkFtZIycNY") )
  }

}
