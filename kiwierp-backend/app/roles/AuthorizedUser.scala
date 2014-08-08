package roles

import models.{User, UserScope}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession
import utils.Route
import utils.exceptions.{AccessForbidden, ResourceNotFound}

import scala.concurrent.Future

trait AuthorizedUser {

  this: User =>

  def checkScope(route: Route)(implicit s: AsyncDBSession): Future[Unit] = {
    def handleNotFound[U]: PartialFunction[Throwable, U] = {
      case e: ResourceNotFound => throw new AccessForbidden
    }

    UserScope.findScope(userType, route.method, route.uri) recover handleNotFound map (_ => ())
  }

}
