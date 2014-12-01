package jsons

import models.Inventory
import play.api.libs.json.{JsValue, Json, Writes}

trait InventoryJson extends KiwiERPJson {

  implicit val inventoryWrites = new Writes[Inventory] {
    def writes(inventory: Inventory): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(inventory.createdAt),
      "description" -> inventory.description,
      "id" -> inventory.id,
      "componentId" -> inventory.componentId,
      "quantity" -> inventory.quantity,
      "updatedAt" -> dateTimeToString(inventory.updatedAt)
    )
  }

}
