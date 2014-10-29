package utils.exceptions

class InvalidToken(val message: String = "") extends KiwiERPException {

  val error = "invalid_token"

  val status = 401

}
