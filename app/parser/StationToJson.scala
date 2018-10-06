package parser

import play.api.libs.json.JsObject
import javax.inject._
import com.google.inject.ImplementedBy
import play.api.libs.json._
import play.api.libs.functional.syntax._

@ImplementedBy(classOf[StationToJsonImpl])
trait StationToJson {
    def formatStationsToJson(stations: List[Station]): JsObject
}

@Singleton
class StationToJsonImpl extends StationToJson{
    implicit val StationWrites: Writes[Station] = (
    (JsPath \ "id").write[Long] and
    (JsPath \ "title").write[String] and
    (JsPath \ "bikes").write[Long] and
    (JsPath \ "locks").write[Long])(unlift(Station.unapply))

  def formatStationsToJson(stations: List[Station]): JsObject = {
    JsObject(Seq("stations" -> Json.toJson(stations)))
  }
}