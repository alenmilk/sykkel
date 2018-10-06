package util

import com.google.inject.ImplementedBy
import javax.inject._

import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

@ImplementedBy(classOf[WSClientCallImpl])
trait WSClientCall {
  def url(url: String): WSRequest
  def executeRequest(request: WSRequest, wait: Int = 30): Try[String]
}

@Singleton
protected class WSClientCallImpl @Inject()(ws: WSClient) extends WSClientCall {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def url(url: String): WSRequest = {
    ws.url(url)
  }

  private def handleResponse(response: WSResponse): String = {
    if (response.status == 200)
      new String(response.body.getBytes, "UTF-8")
    else {
      logger.error("receiving error response: " + new String(response.body.getBytes, "UTF-8"))
      throw new RuntimeException("Httpstatus: " + response.status)
    }
  }

  def executeRequest(request: WSRequest, wait: Int = 30): Try[String] = {
    val futureString = request.get().map(handleResponse)
    val result = Try(Await.result(futureString, wait.second))
    if (result.isFailure) {
      logger.error("Web service call to " + request.url + " failed. ", result.failed.get)
    }
    result
  }
}
