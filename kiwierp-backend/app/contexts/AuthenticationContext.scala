package contexts

import models.{AccessToken, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.AuthenticatedUser
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.Password
import utils.exceptions.InvalidUser

import scala.concurrent.Future

class AuthenticationContext private (user: User, password: Password)(implicit tx: AsyncDBSession) {

  private def authenticate(): Future[AccessToken] = {
    val authenticatedUser = new User(user) with AuthenticatedUser

    authenticatedUser.checkPassword(password)
    authenticatedUser.createAccessToken()
  }

}

object AuthenticationContext extends KiwiERPContext {

  def apply(name: String, password: String): Future[AccessToken] =
    AsyncDB localTx { implicit tx =>
      User.findByName(name) flatMap {
        case Some(user) => new AuthenticationContext(user, new Password(password)).authenticate()
        case None => throw new InvalidUser
      }
    }

}
