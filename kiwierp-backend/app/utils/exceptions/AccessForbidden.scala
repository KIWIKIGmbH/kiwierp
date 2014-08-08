package utils.exceptions

class AccessForbidden extends KiwiERPException {

  val error = "access_forbidden"

  val status = 403

}