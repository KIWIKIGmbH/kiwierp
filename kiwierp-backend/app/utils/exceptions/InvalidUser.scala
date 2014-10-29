package utils.exceptions

class InvalidUser(val message: String = "") extends KiwiERPException {

  val error = "invalid_user"

  val status = 401

}
