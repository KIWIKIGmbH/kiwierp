package jsons

import models.Order
import play.api.libs.json.{JsValue, Json, Writes}

trait OrderJson extends KiwiERPJson {

  implicit val orderWrites = new Writes[Order] {
    def writes(order: Order): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(order.createdAt),
      "deliveredDate" -> optDateTimeToString(order.deliveredDate),
      "id" -> order.id,
      "quantity" -> order.quantity,
      "orderedDate" -> dateTimeToString(order.orderedDate),
      "componentId" -> order.componentId,
      "shippedDate" -> optDateTimeToString(order.shippedDate),
      "status" -> order.status,
      "updatedAt" -> dateTimeToString(order.updatedAt)
    )
  }

}
