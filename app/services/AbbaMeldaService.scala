package services

import java.time.LocalDateTime

import models._
import play.api.Play._
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.libs.ws.WS

import scala.concurrent.Future


object AbbaMeldaService {

  private val onderdeel = "/resources/sync/onderdelen"
  private val onderdelenStatus = "/resources/sync/onderdelenstatus"

  val url = current.configuration.getString("abbamelda.url").get

  import concurrent.Execution.wsExecutionContext

  def fetchStatuses(offset: Int, lastUpdate: LocalDateTime): Future[JsValue] = {

    val limit = current.configuration.getInt("abbamelda.batch.size").getOrElse(20)

    WS.url(url + onderdelenStatus)
      .withQueryString(
        "offset" -> offset.toString,
        "limit" -> limit.toString,
        "lastUpdate" -> dateTimeFormatter(lastUpdate))
      .get()
      .map(_.json)

  }

  def fetchOnderdeel(id:Long) : Future[JsValue] = {
    WS.url(url + onderdeel)
    .withQueryString(
        "id" -> id.toString)
    .get()
    .map(_.json)
  }
}
