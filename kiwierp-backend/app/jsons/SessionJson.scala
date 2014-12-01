package jsons

import models.AccessToken
import play.api.libs.json.{Json, Writes}

trait SessionJson extends KiwiERPJson with UserJson {

  implicit val authenticationWrites = new Writes[AccessToken] {
    def writes(accessToken: AccessToken) = Json.obj(
      "token_type" -> accessToken.tokenType,
      "access_token" -> accessToken.token,
      "expires_in" -> accessToken.expiresIn
    )
  }

}
