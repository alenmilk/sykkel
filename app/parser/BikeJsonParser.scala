package parser

import play.api.libs.json._
import com.google.inject.ImplementedBy
import javax.inject._
import play.api.libs.json.Json



@ImplementedBy(classOf[BikeJsonParserImpl])
trait BikeParser {
  def parseStationTitles(json: JsValue): Map[Long, String]
  def parseAvailableStations(json: JsValue, stations: Map[Long, String]): List[Station]
}

@Singleton
protected class BikeJsonParserImpl extends BikeParser {

  def parseStationTitles(json: JsValue): Map[Long, String] = {
    (json \ "stations").as[JsArray].value.map(s => getStation(s.as[JsObject])).toMap
  }

  def parseAvailableStations(json: JsValue, stations: Map[Long, String]): List[Station] = {
    (json \ "stations").as[JsArray].value.map{ s =>
      val (id, bikes, locks) = getAvailability(s.as[JsObject])
      stations.get(id).map( title => Station(id, title, bikes, locks))
    }.flatten.toList
  }

  def getStation(station: JsObject): (Long, String) = {
    val id: Long = (station \ "id").as[Long]
    val title: String = (station \ "title").as[String]
    (id, title)
  }

  def getAvailability(station: JsObject): (Long, Long, Long) = {
    val id: Long = (station \ "id").as[Long]
    val bikes: Long = (station \ "availability" \ "bikes").as[Long]
    val locks: Long = (station \ "availability" \ "locks").as[Long]
    (id, bikes, locks)
  }

}
