package utils

case class Password(private val password: String) {

  lazy val cryptPassword = {
    val N = 1024
    val r = 8
    val p = 16
    val dkLen = 64

    val salt = play.api.Play.current.configuration.getString("application.salt").get

    com.lambdaworks.crypto.SCrypt.scrypt(password.getBytes("UTF-8"), salt.getBytes("UTF-8"), N, r, p, dkLen)
      .map("%02x".format(_)).mkString
  }

}
