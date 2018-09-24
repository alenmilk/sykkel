import scalaj.http._
import net.liftweb.json._
import net.liftweb.json.DefaultFormats
import scala.util.Try
import scala.util.Success
import scala.util.Failure

object SykkelApp extends App {
  implicit val formats = net.liftweb.json.DefaultFormats
  val stationsUrl: String = "https://oslobysykkel.no/api/v1/stations"
  val availableUrl: String = "https://oslobysykkel.no/api/v1/stations/availability"
  val apiKey = args(0)
  Try(call(stationsUrl, apiKey)) match {
    case Success(responseStations) if responseStations.isSuccess =>
      val stations: Map[Long, String] = parseStationTitles(parse(responseStations.body))

      Try(call(availableUrl, apiKey)) match {
        case Success(responseAvailable) if responseAvailable.isSuccess =>
          val formatedStations: List[String] = parseAvailableStations(parse(responseAvailable.body), stations)
          formatedStations.map(println)
        case Success(responseAvailable) if !responseAvailable.isSuccess =>
          println(s"Error calling url $availableUrl responseAvailable.statusLine")          
        case Failure(exception) =>
          println(s"Error calling url $availableUrl")
          println(exception)
      }
    case Success(responseStations) if !responseStations.isSuccess =>
      println(s"Error calling url $stationsUrl $responseStations.stausLine")
    case Failure(exception) =>
      println(s"Error calling url $stationsUrl")
      println(exception)
  }

  def parseStationTitles(json: JValue) = {
    (json \\ "stations")
      .children(0)
      .children.map(s => getStation(s.asInstanceOf[JObject])).toMap
  }

  def parseAvailableStations(json: JValue, stations: Map[Long, String]): List[String] = {
    (json \\ "stations")
      .children(0)
      .children
      .map { s =>
        val (id, bikes, locks) = getAvailability(s.asInstanceOf[JObject])
        formatStation(id, stations.get(id).getOrElse("ukjent navn"), bikes, locks)
      }
  }

  def call(url: String, apiKey: String): HttpResponse[String] = {
    Http(url)
      .headers("Client-Identifier" -> apiKey)
      .asString
  }

  def getStation(station: JObject): (Long, String) = {
    val id: Long = (station \ "id").extract[Long]
    val title: String = (station \ "title").extract[String]
    (id, title)
  }

  def getAvailability(station: JObject): (Long, Long, Long) = {
    val id: Long = (station \ "id").extract[Long]
    val bikes: Long = (station \ "availability" \ "bikes").extract[Long]
    val locks: Long = (station \ "availability" \ "locks").extract[Long]
    (id, bikes, locks)
  }

  def formatStation(id: Long, title: String, bikes: Long, locks: Long): String = {
    s"$id $title Sykler: $bikes Låser: $locks"
  }

}