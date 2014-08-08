package utils.exceptions

class InvalidToken extends KiwiERPException {

  val error = "invalid_token"

  val status = 401

}