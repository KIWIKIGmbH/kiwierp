package utils.exceptions

class ResourceNotFound extends KiwiERPException {

  val error = "not_found"

  val status = 404

}