package controllers

import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Status
import utils.exceptions.KiwiERPException

import scala.concurrent.Future

object KiwiERPError {

  def result(e: KiwiERPException): Result = {
    val json = Json.obj(
      "error" -> e.error,
      "status" -> e.status
    )

    new Status(e.status)(json)
  }

  def futureResult(e: KiwiERPException): Future[Result] = Future.successful(result(e))

}
