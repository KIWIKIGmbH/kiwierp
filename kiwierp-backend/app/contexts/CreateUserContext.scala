package contexts

import models.User
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.CreateAccessUser
import scalikejdbc.async.{AsyncDBSession, AsyncDB}
import utils.Password

import scala.concurrent.Future

class CreateUserContext (name: String, password: Password, userType: String, user: User)(implicit s: AsyncDBSession) {

  private def create(): Future[User] = {
    val createAccessUser = new User(user) with CreateAccessUser

    createAccessUser.checkUserType(userType)

    createAccessUser.checkUserNotExist(name) flatMap { _ =>
      createAccessUser.create(name, password, userType)
    }
  }

}

object CreateUserContext extends KiwiERPContext {

  def apply(name: String, password: String, userType: String, optUser: Option[User]): Future[User] = AsyncDB withPool { implicit s =>
    new CreateUserContext(name, new Password(password), userType, optUser.get).create()
  }

}
