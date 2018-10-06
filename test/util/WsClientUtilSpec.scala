package util

import org.scalatest.mock._
import org.scalatestplus.play._
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.ArgumentMatchers

import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.libs.ws.WSResponse
import play.api.http._
import scala.util.{Failure, Success}
import scala.concurrent.Future
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

class WsClientUtilSpec extends PlaySpec with org.scalatest.mockito.MockitoSugar {

  "url" must {
    "success" in {

      val wsMock = mock[WSClient]
      val request = mock[WSRequest]
      when(wsMock.url(ArgumentMatchers.anyString())).thenReturn(request)

      val wsClient = new WSClientCallImpl(wsMock)

      wsClient.url("testing") mustBe request
      verify(wsMock, times(1)).url("testing")
    }
  }

  val httpBody = "http body response"
  "executeRequest og postRequest" must {
    "success" in {
      val wsMock = mock[WSClient]
      val request = mock[WSRequest]
      val response = mock[WSResponse]
      when(response.status) thenReturn 200
      when(response.body) thenReturn httpBody

      when(request.get()) thenReturn Future { response }


      when(request.withFollowRedirects(ArgumentMatchers.anyBoolean())) thenReturn request
      when(wsMock.url(ArgumentMatchers.anyString())) thenReturn request

      val wsClient = new WSClientCallImpl(wsMock)
      val getResult = wsClient.executeRequest(request, 30)
      getResult match {
        case Success(v) => v mustBe httpBody
        case Failure(e) => assert(false)
      }
      verify(request, times(1)).get()
      verify(response, times(1)).status
      verify(response, times(1)).body


    }
    "response 100" in {
      val wsMock = mock[WSClient]
      val request = mock[WSRequest]
      val response = mock[WSResponse]
      when(response.status) thenReturn 100
      when(response.body) thenReturn httpBody

      when(request.get()) thenReturn Future { response }

      when(request.withFollowRedirects(ArgumentMatchers.anyBoolean())) thenReturn request
      when(wsMock.url(ArgumentMatchers.anyString())) thenReturn request

      val wsClient = new WSClientCallImpl(wsMock)
      val getResult = wsClient.executeRequest(request, 30)
      assert(getResult.isFailure)
      verify(request, times(1)).get()

    }
  }
}
