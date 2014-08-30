package utils.exceptions

trait KiwiERPException extends RuntimeException {

  val error: String

  val status: Int

  val message: String

}
