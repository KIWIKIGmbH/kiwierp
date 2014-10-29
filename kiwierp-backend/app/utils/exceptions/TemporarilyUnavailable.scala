package utils.exceptions

class TemporarilyUnavailable(val message: String = "") extends KiwiERPException {

  val error = "temporarily_unavailable"

  val status = 503

}
