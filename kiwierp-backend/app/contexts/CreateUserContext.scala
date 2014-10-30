package contexts

import models.User
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.AddAccessUser
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.Password

import scala.concurrent.Future

class CreateUserContext
(name: String,
 password: Password,
 userType: String,
 user: User)(implicit s: AsyncDBSession) {

  private def create(): Future[User] = {
    val addAccessUser = new User(user) with AddAccessUser

    addAccessUser.checkUserType(userType)

    addAccessUser.checkUserNotExist(name) flatMap { _ =>
      addAccessUser.create(name, password, userType)
    }
  }

}

object CreateUserContext extends KiwiERPContext {

  def apply(name: String, password: String, userType: String, optUser: Option[User]): Future[User] =
    AsyncDB withPool { implicit s =>
      val user = optUser.get
      val cxt = new CreateUserContext(name, new Password(password), userType, user)

      cxt.create()
    }

}
