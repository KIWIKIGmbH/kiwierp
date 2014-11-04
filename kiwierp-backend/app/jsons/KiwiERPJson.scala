package jsons

import org.joda.time.DateTime

trait KiwiERPJson {

  private val DATETIME_PATTERN = "yyyy-MM-dd HH:mm"

  def dateTimeToString(dateTime: DateTime): String = dateTime.toString(DATETIME_PATTERN)

  def optDateTimeToString(optDateTime: Option[DateTime]): Option[String] =
    optDateTime map dateTimeToString

}
