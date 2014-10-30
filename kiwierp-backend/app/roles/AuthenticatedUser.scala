package roles

import models.{AccessToken, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession
import utils.Password
import utils.exceptions.InvalidUser

import scala.concurrent.Future

trait AuthenticatedUser {

  this: User =>

  def checkPassword(password: Password) =
    if (password.cryptPassword != this.password) throw new InvalidUser

  def createAccessToken()(implicit s: AsyncDBSession): Future[AccessToken] =
    AccessToken.destroyByUserId(id) flatMap (_ => AccessToken.createByUserId(id))

}
