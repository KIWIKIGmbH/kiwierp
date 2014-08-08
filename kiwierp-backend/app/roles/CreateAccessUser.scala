package roles

import models.User
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession
import utils.Password
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait CreateAccessUser {

  this: User =>

  def checkUserType(userType: String): Unit = {
    userType match {
      case "admin" | "user" | "guest" => ()
      case _ => throw new InvalidRequest
    }
  }

  def checkUserNotExist(name: String)(implicit s: AsyncDBSession): Future[Unit] = User.findByName(name) map { optUser =>
    if (optUser.isDefined) throw new InvalidRequest else ()
  }

  def create(name: String, password: Password, userType: String)(implicit s: AsyncDBSession): Future[User] =
    User.create(name, password.cryptPassword, userType)

}
