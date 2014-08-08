package jsons

import org.joda.time.DateTime
import play.api.libs.json.{JsObject, Json}

trait KiwiERPJson[M] {

  val DATETIME_PATTERN = "yyyy-MM-dd HH:mm"

  def dateTimeToString(dateTime: DateTime): String = dateTime.toString(DATETIME_PATTERN)

  def optDateTimeToString(optDateTime: Option[DateTime]): Option[String] = optDateTime map dateTimeToString

  def base(m: M): JsObject

  def index(ms: List[M], page: Int): JsObject = Json.obj(
    "results" -> Json.toJson(ms map base),
    "count" -> ms.size,
    "page" -> page
  )

  def create(m: M): JsObject = base(m)

  def read(m: M): JsObject =  base(m)

}
