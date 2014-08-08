package roles

import models.User

trait ReadAccessUser {

  this: User =>

  def hasPermission(id: Long): Boolean = isAdmin || id == this.id

}
