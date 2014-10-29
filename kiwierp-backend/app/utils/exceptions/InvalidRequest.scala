package utils.exceptions

class InvalidRequest(val message: String = "") extends KiwiERPException {

  val error = "invalid_request"

  val status = 400

}
