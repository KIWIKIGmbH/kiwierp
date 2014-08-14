package controllers

import jsons.ErrorJson
import play.api.mvc.Result
import play.api.mvc.Results.Status
import utils.exceptions.KiwiERPException

import scala.concurrent.Future

object KiwiERPError {

  def result(e: KiwiERPException): Result = new Status(e.status)(ErrorJson(e))

  def futureResult(e: KiwiERPException): Future[Result] = Future.successful(result(e))

}
