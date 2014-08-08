package models

import models.daos.UserScopeDAO
import org.joda.time.DateTime

case class UserScope
(method: String,
 uri: String,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None)

object UserScope extends UserScopeDAO
