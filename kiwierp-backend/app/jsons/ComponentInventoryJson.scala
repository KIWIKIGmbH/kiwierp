package jsons

import models.ComponentInventory
import play.api.libs.json.{JsValue, Json, Writes}

trait ComponentInventoryJson extends KiwiERPJson {

  implicit val inventoryWrites = new Writes[ComponentInventory] {
    def writes(inventory: ComponentInventory): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(inventory.createdAt),
      "description" -> inventory.description,
      "id" -> inventory.id,
      "componentId" -> inventory.componentId,
      "quantity" -> inventory.quantity,
      "updatedAt" -> dateTimeToString(inventory.updatedAt)
    )
  }

}
