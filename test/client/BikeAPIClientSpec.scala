package client

import scala.util.Success

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec

import play.api.Configuration
import play.api.libs.ws.WSRequest
import util.WSClientCall
import scala.util.Failure

class BikeAPIClientSpec extends PlaySpec with org.scalatest.mockito.MockitoSugar {
  val config = Configuration(
    "stationAPI.generalInfo" -> "test",
    "stationAPI.availability" -> "test",
    "stationAPI.apiKey" -> "test")

  "call" must {
    "malformed url returns failure" in {
      val wsClientMock = mock[WSClientCall]
      when(wsClientMock.url(any())).thenThrow(new RuntimeException)
      val bikeClient = new BikeAPIClientImpl(wsClientMock, config)
      bikeClient.call("dsdsadas").isFailure mustBe true
    }

    "url ok execute success returns Success" in {
      val wsClientMock = mock[WSClientCall]
      val requestMock = mock[WSRequest]
      when(wsClientMock.url(any())).thenReturn(requestMock)
      when(requestMock.addHttpHeaders(any())).thenReturn(requestMock)
      when(wsClientMock.executeRequest(any(), any())).thenReturn(Success("OK"))
      val bikeClient = new BikeAPIClientImpl(wsClientMock, config)
      bikeClient.call("http://test.com") mustBe Success("OK")
    }

    "url ok execute failure returns Failure" in {
      val wsClientMock = mock[WSClientCall]
      val requestMock = mock[WSRequest]
      when(wsClientMock.url(any())).thenReturn(requestMock)
      when(requestMock.addHttpHeaders(any())).thenReturn(requestMock)
      when(wsClientMock.executeRequest(any(), any())).thenReturn(Failure(new RuntimeException))
      val bikeClient = new BikeAPIClientImpl(wsClientMock, config)
      bikeClient.call("http://test.com").isFailure mustBe true
    }
  }

}