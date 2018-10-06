package controllers

import play.api.mvc.InjectedController
import org.slf4j.LoggerFactory
import javax.inject._
import util.WSClientCall
import scala.util.Try
import scala.util.Success
import parser.BikeParser
import play.api.libs.json.Json
import scala.util.Failure
import parser.Station
import play.api.libs.json._
import play.api.libs.functional.syntax._

class Application @Inject() (ws: WSClientCall, parser: BikeParser, implicit val configuration: play.api.Configuration) extends InjectedController {

  val logger = LoggerFactory.getLogger(this.getClass)

  val stationsUrl: String = configuration.get[String]("stationAPI.generalInfo")
  val availableUrl: String = configuration.get[String]("stationAPI.availability")
  val apiKey: String = configuration.get[String]("stationAPI.apiKey")
  implicit val StationWrites: Writes[Station] = (
    (JsPath \ "id").write[Long] and
    (JsPath \ "title").write[String] and
    (JsPath \ "bikes").write[Long] and 
    (JsPath \ "locks").write[Long])(unlift(Station.unapply))

  def sykkeldata = Action {
    implicit request =>
      Try(call(stationsUrl, apiKey)) match {
        case Success(responseStations) if responseStations.isSuccess =>
          val stationTitles: Map[Long, String] = parser.parseStationTitles(Json.parse(responseStations.get))

          Try(call(availableUrl, apiKey)) match {
            case Success(responseAvailable) if responseAvailable.isSuccess =>
              val stations: List[Station] = parser.parseAvailableStations(Json.parse(responseAvailable.get), stationTitles)
              Ok(JsObject(Seq("stations" -> Json.toJson(stations))))
            case Success(responseAvailable) if !responseAvailable.isSuccess =>
              logger.error(s"Error calling url $availableUrl responseAvailable.statusLine")
              InternalServerError("Klarte ikke å hente data")
            case Failure(exception) =>
              logger.error(s"Error calling url $availableUrl", exception)
              InternalServerError("Error")
          }
        case Success(responseStations) if !responseStations.isSuccess =>
          logger.error(s"Error calling url $stationsUrl $responseStations.stausLine")
          InternalServerError("Error")
        case Failure(exception) =>
          logger.error(s"Error calling url $stationsUrl", exception)
          InternalServerError("Error")
      }

  }

  def call(url: String, apiKey: String): Try[String] = {
    val req = ws.url(url).addHttpHeaders("Client-Identifier" -> apiKey)
    ws.executeRequest(req)
  }


}