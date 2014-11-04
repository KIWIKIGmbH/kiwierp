package jsons

import models.InventoryOrder
import play.api.libs.json.{JsValue, Json, Writes}

trait InventoryOrderJson extends KiwiERPJson {

  implicit val inventoryOrderWrites = new Writes[InventoryOrder] {
    def writes(inventoryOrder: InventoryOrder): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(inventoryOrder.createdAt),
      "deliveredDate" -> optDateTimeToString(inventoryOrder.deliveredDate),
      "id" -> inventoryOrder.id,
      "quantity" -> inventoryOrder.quantity,
      "orderedDate" -> dateTimeToString(inventoryOrder.orderedDate),
      "partsId" -> inventoryOrder.partsId,
      "shippedDate" -> optDateTimeToString(inventoryOrder.shippedDate),
      "status" -> inventoryOrder.status,
      "updatedAt" -> dateTimeToString(inventoryOrder.updatedAt)
    )
  }

}
