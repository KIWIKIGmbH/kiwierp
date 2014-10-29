package utils.exceptions

class AccessForbidden(val message: String = "") extends KiwiERPException {

  val error = "access_forbidden"

  val status = 403

}
