package utils.exceptions

class ServerError(val message: String = "") extends KiwiERPException {

  val error = "server_error"

  val status = 500

}
