package controllers

import play.api.mvc.InjectedController
import org.slf4j.LoggerFactory
import javax.inject._
import scala.util.Try
import scala.util.Success
import parser.BikeParser
import play.api.libs.json.Json
import scala.util.Failure
import parser.Station
import parser.StationToJson
import client.BikeAPIClient
import play.api.mvc.Result


class Application @Inject()( 
    parser: BikeParser, 
    generateStationJson: StationToJson,
    bikeAPIClient : BikeAPIClient    
    ) extends InjectedController {

  val logger = LoggerFactory.getLogger(this.getClass)

  def sykkeldata = Action {    
    generateResult()
  }

  def generateResult():Result = {
      Try(bikeAPIClient.getStations) match {
        case Success(responseStations) if responseStations.isSuccess =>
          val stationTitles: Map[Long, String] = parser.parseStationTitles(Json.parse(responseStations.get))

          Try(bikeAPIClient.getAvailable) match {
            case Success(responseAvailable) if responseAvailable.isSuccess =>
              val stations: List[Station] = parser.parseAvailableStations(Json.parse(responseAvailable.get), stationTitles)
              Ok(generateStationJson.formatStationsToJson(stations))
            case Success(responseAvailable) if !responseAvailable.isSuccess =>
              logger.error(s"Error calling url $bikeAPIClient.availableUrl responseAvailable.statusLine")
              InternalServerError("Klarte ikke å hente data")
            case Failure(exception) =>
              logger.error(s"Error calling url $bikeAPIClient.availableUrl", exception)
              InternalServerError("Serverfeil")
          }
        case Success(responseStations) if !responseStations.isSuccess =>
          logger.error(s"Error calling url $bikeAPIClient.stationsUrl $responseStations.stausLine")
          InternalServerError("Klarte ikke å hente data")
        case Failure(exception) =>
          logger.error(s"Error calling url $bikeAPIClient.stationsUrl", exception)
          InternalServerError("Serverfeil")
      }
    
  }

}