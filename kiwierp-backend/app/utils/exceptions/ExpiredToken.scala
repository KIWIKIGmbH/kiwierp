package utils.exceptions

class ExpiredToken extends KiwiERPException {

  val error = "expired_token"

  val status = 401

}