package jsons

import models.InventoryConsumption
import play.api.libs.json.Json

object InventoryConsumptionJson extends KiwiERPJson[InventoryConsumption] {

  def base(inventoryConsumption: InventoryConsumption) = Json.obj(
    "consumed" -> inventoryConsumption.consumed,
    "createdAt" -> inventoryConsumption.createdAt,
    "id" -> inventoryConsumption.id,
    "productId" -> inventoryConsumption.productId,
    "updatedAt" -> inventoryConsumption.updatedAt
  )

}
