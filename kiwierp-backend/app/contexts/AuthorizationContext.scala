package contexts

import models.{AccessToken, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{AuthorizedAccessToken, AuthorizedUser}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.Route
import utils.exceptions.InvalidToken

import scala.concurrent.Future

class AuthorizationContext private (accessToken: AccessToken, user: User, route: Route)(implicit s: AsyncDBSession) {

  private def authorize(): Future[Unit] = {
    val authorizedUser = new User(user) with AuthorizedUser
    val authorizedAccessToken = new AccessToken(accessToken) with AuthorizedAccessToken

    authorizedAccessToken.checkExpiration()
    authorizedUser.checkScope(route)
  }

}

object AuthorizationContext extends KiwiERPContext {

  def apply[A](token: String, method: String, path: String): Future[AccessToken] = AsyncDB withPool { implicit s =>
    AccessToken.findWithUserByToken(token) recover handleNotFound(new InvalidToken) flatMap { accessToken =>
      val user = accessToken.user.get

      new AuthorizationContext(accessToken, user, new Route(method, path)).authorize() map (_ => accessToken)
    }
  }

}
