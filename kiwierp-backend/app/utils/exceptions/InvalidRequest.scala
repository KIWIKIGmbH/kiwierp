package utils.exceptions

class InvalidRequest extends KiwiERPException {

  val error = "invalid_request"

  val status = 400

}