package services

import java.time.LocalDateTime

import models._
import play.api.Play._
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.libs.ws.WS

import scala.concurrent.Future


object AbbaMeldaService {

  private val onderdelenStatus = "/resources/sync/onderdelenstatus"

  def fetchStatuses(offset: Int, lastUpdate: LocalDateTime): Future[JsValue] = {

    val url = current.configuration.getString("abbamelda.url").get
    val limit = current.configuration.getInt("abbamelda.batch.size").getOrElse(20)

    WS.url(url + onderdelenStatus)
      .withQueryString(
        "offset" -> offset.toString,
        "limit" -> limit.toString,
        "lastUpdate" -> dateTimeFormatter(lastUpdate))
      .get()
      .map(_.json)

  }
}
