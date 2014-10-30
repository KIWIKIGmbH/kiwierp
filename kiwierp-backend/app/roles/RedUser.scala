package roles

import models.User
import utils.exceptions.AccessForbidden

trait RedUser {

  this: User =>

  def checkPermission(readAccessUser: User with ReadAccessUser) =
    if (!readAccessUser.hasPermission(id)) throw new AccessForbidden

}
