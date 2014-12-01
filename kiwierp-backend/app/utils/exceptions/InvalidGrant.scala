package utils.exceptions

class InvalidGrant(val message: String = "") extends KiwiERPException {

  val error = "invalid_grant"

  val status = 401

}
