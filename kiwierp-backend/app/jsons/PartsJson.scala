package jsons

import models.Parts
import play.api.libs.json.{JsValue, Json, Writes}

trait PartsJson extends KiwiERPJson with InventoryJson with InventoryOrderJson {

  implicit val partsWrites = new Writes[Parts] {
    def writes(parts: Parts): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(parts.createdAt),
      "description" -> parts.description,
      "id" -> parts.id,
      "name" -> parts.name,
      "neededQuantity" -> parts.neededQuantity,
      "productId" -> parts.productId,
      "unclassifiedQuantity" -> parts.unclassifiedQuantity,
      "updatedAt" -> dateTimeToString(parts.updatedAt),
      "inventories" -> Json.toJson(parts.inventories),
      "inventoryOrders" -> Json.toJson(parts.inventoryOrders)
    )
  }

}
