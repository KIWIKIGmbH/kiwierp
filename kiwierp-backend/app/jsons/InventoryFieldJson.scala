package jsons

import models.InventoryField
import play.api.libs.json.Json

object InventoryFieldJson extends KiwiERPJson[InventoryField] {

  def base(inventoryField: InventoryField) = Json.obj(
    "createdAt" -> inventoryField.createdAt,
    "fieldType" -> inventoryField.fieldType,
    "isRequired" -> inventoryField.isRequired,
    "max" -> inventoryField.max,
    "min" -> inventoryField.min,
    "name" -> inventoryField.name,
    "productId" -> inventoryField.productId,
    "updatedAt" -> inventoryField.updatedAt
  )

}
