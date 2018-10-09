package controllers

import org.scalatestplus.play.PlaySpec
import client.BikeAPIClient
import scala.util.{ Failure, Success }
import org.mockito.Mockito._
import play.api.mvc.Result._
import play.api.mvc.Results
import parser.BikeParser
import org.mockito.ArgumentMatchers.any
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import parser.StationToJson
import play.api.libs.json.JsObject
import parser.Station

class ApplicationSpec extends PlaySpec with org.scalatest.mockito.MockitoSugar {
  "generateResult" must {
    "getStations exception returns 500" in {
      val clientMock = mock[BikeAPIClient]
      when(clientMock.getStations).thenReturn(Failure(new Exception))

      val app = new Application(null, null, clientMock)
      val result = app.generateResult
      result.header.status mustBe 500
    }

    "getAvailable exception returns 500" in {
      val clientMock = mock[BikeAPIClient]
      val bikeParserMock = mock[BikeParser]

      when(clientMock.getStations).thenReturn(Success("{}"))
      when(clientMock.getAvailable).thenReturn(Failure(new Exception))
      when(bikeParserMock.parseStationTitles(any()))
        .thenReturn(Map[Long, String]())

      val app = new Application(bikeParserMock, null, clientMock)
      val result = app.generateResult
      result.header.status mustBe 500
    }

    "both return Success returns 200" in {
      val clientMock = mock[BikeAPIClient]
      val toJsonMock = mock[StationToJson]
      val bikeParserMock = mock[BikeParser]
      when(clientMock.getStations)
        .thenReturn(Success("{}"))
      when(clientMock.getAvailable)
        .thenReturn(Success("{}"))
      when(bikeParserMock.parseAvailableStations(any(), any()))
        .thenReturn(List())
      when(bikeParserMock.parseStationTitles(any()))
        .thenReturn(Map[Long, String]())
      when(toJsonMock.formatStationsToJson(any()))
        .thenReturn(JsObject(Seq()))

      val app = new Application(bikeParserMock, toJsonMock, clientMock)
      val result = app.generateResult
      result.header.status mustBe 200
    }
  }
}