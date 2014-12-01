package utils

case class Route(method: String, private val path: String) {

  private lazy val PATH_REGEX = {
    val appContext = "/api/v[1-9]\\d*"
    val resource = "/[a-z/\\-]+[a-z]"
    s"""$appContext($resource)""".r
  }

  private lazy val PATH_REGEX_WITH_ID = {
    val id = "[1-9]\\d*"
    s"""$PATH_REGEX/$id""".r
  }

  private lazy val PATH_REGEX_WITH_ID_AND_EXTRA = {
    val extra = "[a-z]+"
    s"""$PATH_REGEX_WITH_ID/($extra)""".r
  }

  val uri = path match {
    case PATH_REGEX_WITH_ID_AND_EXTRA(resource, extra) => s"$resource/:id/$extra"
    case PATH_REGEX_WITH_ID(resource) => s"$resource/:id"
    case PATH_REGEX(resource) => resource
  }

}
