package jsons

import play.api.libs.json.Json
import utils.exceptions.KiwiERPException

object ErrorJson {

  def apply(e: KiwiERPException) = Json.obj(
    "error" -> e.error,
    "message" -> e.message,
    "status" -> e.status
  )

}
