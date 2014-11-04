package jsons

import models.{AccessToken, User}
import play.api.libs.json.{JsValue, Json, Writes}

trait UserJson extends KiwiERPJson {

  implicit val userWrites = new Writes[User] {
    def writes(user: User): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(user.createdAt),
      "id" -> user.id,
      "name" -> user.name,
      "userType" -> user.userType,
      "updatedAt" -> dateTimeToString(user.updatedAt)
    )
  }

  implicit val authenticationWrites = new Writes[AccessToken] {
    def writes(accessToken: AccessToken) = Json.obj(
      "createdAt" -> dateTimeToString(accessToken.createdAt),
      "expiresIn" -> accessToken.expiresIn,
      "token" -> accessToken.token,
      "tokenType" -> accessToken.tokenType,
      "userId" -> accessToken.userId
    )
  }

}
