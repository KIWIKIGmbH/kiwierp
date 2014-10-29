package utils.exceptions

class ExpiredToken(val message: String = "") extends KiwiERPException {

  val error = "expired_token"

  val status = 401

}
