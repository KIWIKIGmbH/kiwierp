package utils.exceptions

class InvalidUser extends KiwiERPException {

  val error = "invalid_user"

  val status = 401

}