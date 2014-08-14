package utils.exceptions

class APINotImplemented(val message: String = "") extends KiwiERPException {

  val error = "api_not_implemented"

  val status = 501

}