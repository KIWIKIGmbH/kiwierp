package models

import models.daos.UserDAO
import org.joda.time.DateTime

case class User
(id: Long,
 name: String,
 password: String,
 userType: String,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None) {

  def this(user: User) = this(
    user.id,
    user.name,
    user.password,
    user.userType,
    user.createdAt,
    user.updatedAt,
    user.deletedAt
  )

  def isAdmin: Boolean = userType == "admin"

}

object User extends UserDAO
