package roles

import models.User
import utils.exceptions.AccessForbidden

trait RedUser {

  this: User =>

  def checkPermission(readAccessUser: User with ReadAccessUser): Unit =
    if (!readAccessUser.hasPermission(id)) throw new AccessForbidden

}
