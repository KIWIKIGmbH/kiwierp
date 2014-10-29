import controllers.KiwiERPError
import play.api.GlobalSettings
import play.api.mvc.RequestHeader
import utils.exceptions.{InvalidRequest, ResourceNotFound, ServerError}

object Global extends GlobalSettings {

  override def onHandlerNotFound(rh: RequestHeader) = KiwiERPError.futureResult(new ResourceNotFound)

  override def onBadRequest(rh: RequestHeader, error: String) = KiwiERPError.futureResult(new InvalidRequest)

  override def onError(rh: RequestHeader, ex: Throwable) = KiwiERPError.futureResult(new ServerError)

}
