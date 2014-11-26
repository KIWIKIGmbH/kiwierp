package jsons

import models.AccessToken
import play.api.libs.json.{Json, Writes}

trait SessionJson extends KiwiERPJson {

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
