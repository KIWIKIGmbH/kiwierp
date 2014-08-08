package utils.exceptions

class APINotImplemented extends KiwiERPException {

  val error = "api_not_implemented"

  val status = 501

}