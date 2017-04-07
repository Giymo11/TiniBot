package science.wasabi.tini.bot.discord

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpRequest, _}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import science.wasabi.tini.bot.Config
import science.wasabi.tini.bot.discord.DiscordActor._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}



object DiscordActor {
  case class StartDiscord(token: String)
  case class GatewayResponse(url: String, shards: Int)
  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val gatewayResponseFormat: RootJsonFormat[GatewayResponse] = jsonFormat2(GatewayResponse)
  }
}

class DiscordActor extends Actor with JsonSupport {

  val log = Logging(context.system, this)

  var token: String = _

  override def preStart() = {
    log.debug("Starting")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }

  val http = Http(context.system)
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  import scala.concurrent.ExecutionContext.Implicits.global

  override def receive: Receive = {
    case StartDiscord(token) =>
      this.token = token
      log.info("Received token: {}", token)

      val discordBaseUri = "https://discordapp.com/api"
      val authorization = "Bot " + token

      val userAgent = s"DiscordBot (${Config.homepage}, ${Config.version})"

      val request = HttpRequest(
        HttpMethods.GET,
        discordBaseUri + "/gateway/bot",
        List[HttpHeader](
          RawHeader("Authorization", authorization),
          `User-Agent`(userAgent)
        )
      )

      context.become(waitingForResponse)
      http.singleRequest(request) pipeTo self

    case x =>
      log.warning("Received unknown message: {}", x)
  }

  def waitingForResponse: Receive = {
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      log.info("trying to unmarshal")
      Unmarshal(entity).to[GatewayResponse] pipeTo self

    case resp @ HttpResponse(code, _, _, _) =>
      log.info("Request failed, response code: " + code)
      resp.discardEntityBytes()
      context.unbecome()

    case GatewayResponse(url, shards) =>

      val actualShards = if(shards < 2) 2 else shards

      log.info("Sharding to " + actualShards + " shards")

      for(i <- 0 until actualShards) {
        val child = context.actorOf(Props[ShardActor], "ShardActor-" + i)
        // TODO: maybe check token for null? should not be possible tho...
        child ! ShardActor.StartShard(token, i, actualShards)
      }

      context.become(initialized)

    case x =>
      log.warning("the fuck is this " + x)
  }

  def initialized: Receive = {
    case x =>
      log.warning("Initialized! " + x)
  }
}




