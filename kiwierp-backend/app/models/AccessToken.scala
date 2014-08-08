package models

import models.daos.AccessTokenDAO
import org.joda.time.DateTime

import scala.concurrent.Future

case class AccessToken
(token: String,
 userId: Long,
 expiresIn: Int,
 tokenType: String,
 createdAt: DateTime,
 user: Option[User] = None) {

  def this(accessToken: AccessToken) = this(
    accessToken.token,
    accessToken.userId,
    accessToken.expiresIn,
    accessToken.tokenType,
    accessToken.createdAt,
    accessToken.user
  )

  def isExpired: Boolean = createdAt.plusSeconds(expiresIn).isBeforeNow

}

object AccessToken extends AccessTokenDAO {

  def createByUserId(userId: Long)(implicit session: ADS): Future[AccessToken] = {
    val EXPIRES_IN = 60 * 60 * 24 * 7
    val TOKEN_TYPE = "Bearer"

    def generateToken: String = {
      val ALGORITHM = "SHA-256"
      val MINIMUM_TOKEN_LENGTH = 55

      val messageDigest = java.security.MessageDigest.getInstance(ALGORITHM)
      val input = java.util.UUID.randomUUID().toString.getBytes
      val baseToken = messageDigest.digest(input).map("%02x".format(_)).mkString

      baseToken.substring(scala.util.Random.nextInt(baseToken.length - MINIMUM_TOKEN_LENGTH))
    }

    create(generateToken, userId, EXPIRES_IN, TOKEN_TYPE)
  }

}