package jsons

import models.Inventory
import play.api.libs.json.Json

object InventoryJson extends KiwiERPJson[Inventory] {

  def base(inventory: Inventory) = Json.obj(
    "createdAt" -> inventory.createdAt,
    "description" -> inventory.description,
    "id" -> inventory.id,
    "quantity" -> inventory.quantity,
    "updatedAt" -> inventory.updatedAt
  )

}
