package utils.exceptions

class TemporarilyUnavailable extends KiwiERPException {

  val error = "temporarily_unavailable"

  val status = 503

}