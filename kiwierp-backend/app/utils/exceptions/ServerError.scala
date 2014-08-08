package utils.exceptions

class ServerError extends KiwiERPException {

  val error = "server_error"

  val status = 500

}