package jsons

import models.{AccessToken, User}
import play.api.libs.json.Json

object UserJson extends KiwiERPJson[User] {

  def base(user: User) = Json.obj(
    "createdAt" -> user.createdAt,
    "id" -> user.id,
    "name" -> user.name,
    "userType" -> user.userType,
    "updatedAt" -> user.updatedAt
  )

  def authenticate(accessToken: AccessToken) = Json.obj(
    "createdAt" -> accessToken.createdAt,
    "expiresIn" -> accessToken.expiresIn,
    "token" -> accessToken.token,
    "tokenType" -> accessToken.tokenType,
    "userId" -> accessToken.userId
  )

}
