package roles

import models.{AccessToken, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession
import utils.Password
import utils.exceptions.{InvalidGrant, InvalidUser}

import scala.concurrent.Future

trait AuthenticatedUser {

  this: User =>

  def checkPassword(password: Password) =
    if (password.cryptPassword != this.password) throw new InvalidUser

  def checkGrantType(grantType: String) =
    if (grantType != "password") throw new InvalidGrant

  def getOrCreateAccessToken()(implicit s: AsyncDBSession): Future[AccessToken] =
    AccessToken.findByUserId(id) flatMap {
      case Some(accessToken) if !accessToken.isExpired =>
        Future.successful(accessToken)
      case Some(accessToken) =>
        AccessToken.destroyByToken(accessToken.token)
        AccessToken.createByUserId(id)
      case None =>
        AccessToken.createByUserId(id)
    }

}
