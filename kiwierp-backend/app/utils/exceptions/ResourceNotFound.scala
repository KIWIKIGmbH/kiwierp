package utils.exceptions

class ResourceNotFound(val message: String = "") extends KiwiERPException {

  val error = "not_found"

  val status = 404

}
