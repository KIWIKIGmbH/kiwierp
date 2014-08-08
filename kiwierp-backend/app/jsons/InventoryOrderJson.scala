package jsons

import models.InventoryOrder
import play.api.libs.json.Json

object InventoryOrderJson extends KiwiERPJson[InventoryOrder] {

 def base(inventoryOrder: InventoryOrder) = Json.obj(
    "createdAt" -> inventoryOrder.createdAt,
    "deliveredDate" -> optDateTimeToString(inventoryOrder.deliveredDate),
    "id" -> inventoryOrder.id,
    "quantity" -> inventoryOrder.quantity,
    "orderedDate" -> dateTimeToString(inventoryOrder.orderedDate),
    "partsId" -> inventoryOrder.partsId,
    "shippedDate" -> optDateTimeToString(inventoryOrder.shippedDate),
    "status" -> inventoryOrder.status,
    "updatedAt" -> inventoryOrder.updatedAt
  )

}
