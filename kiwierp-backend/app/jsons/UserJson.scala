package jsons

import models.User
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

}
