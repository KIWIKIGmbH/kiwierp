package jsons

import models.{Inventory, Parts}
import play.api.libs.json.Json

object PartsJson extends KiwiERPJson[Parts] {

  def base(parts: Parts) = Json.obj(
    "createdAt" -> parts.createdAt,
    "description" -> parts.description,
    "id" -> parts.id,
    "name" -> parts.name,
    "neededQuantity" -> parts.neededQuantity,
    "productId" -> parts.productId,
    "unclassifiedQuantity" -> parts.unclassifiedQuantity,
    "updatedAt" -> parts.updatedAt
  )

  def classify(inventory: Inventory) = InventoryJson.create(inventory)

}
