package jsons

import models.InventoryFieldValue
import play.api.libs.json.Json

object InventoryFieldValueJson extends KiwiERPJson[InventoryFieldValue] {

  def base(inventoryFieldValue: InventoryFieldValue) = Json.obj(
    "createdAt" -> inventoryFieldValue.createdAt,
    "inventoryId" -> inventoryFieldValue.inventoryId,
    "inventoryFieldId" -> inventoryFieldValue.inventoryFieldId,
    "updatedAt" -> inventoryFieldValue.updatedAt,
    "value" -> inventoryFieldValue.value
  )

}
