package client

import util.WSClientCall
import com.google.inject.ImplementedBy
import javax.inject._
import scala.util.Try
import scala.util.Success
import scala.util.Failure

@ImplementedBy(classOf[BikeAPIClientImpl])
trait BikeAPIClient {
  def stationsUrl: String
  def availableUrl: String
  def getStations: Try[String]
  def getAvailable: Try[String]
}

@Singleton
class BikeAPIClientImpl @Inject() (ws: WSClientCall, implicit val configuration: play.api.Configuration) extends BikeAPIClient {
  def stationsUrl: String = configuration.get[String]("stationAPI.generalInfo")
  def availableUrl: String = configuration.get[String]("stationAPI.availability")
  def apiKey: String = configuration.get[String]("stationAPI.apiKey")

  def getStations = call(stationsUrl)
  def getAvailable = call(availableUrl)

  def call(url: String): Try[String] = {
    val executeTry: Try[Try[String]] = Try {
      val req = ws.url(url).addHttpHeaders("Client-Identifier" -> apiKey)
      ws.executeRequest(req)
    }
    executeTry.flatten
  }

}