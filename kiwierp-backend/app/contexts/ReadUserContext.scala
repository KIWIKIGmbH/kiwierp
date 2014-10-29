package contexts

import models.User
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{ReadAccessUser, RedUser}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}

import scala.concurrent.Future

class ReadUserContext private (red: User, access: User)(implicit s: AsyncDBSession) {

  private def read(): User = {
    val redUser = new User(red) with RedUser
    val readAccessUser = new User(access) with ReadAccessUser

    redUser.checkPermission(readAccessUser)
    redUser
  }

}

object ReadUserContext {

  def apply[A](id: Long, optAccess: Option[User]): Future[User] = AsyncDB withPool { implicit s =>
    User.find(id) map { user =>
      new ReadUserContext(user, optAccess.get).read()
    }
  }

}
