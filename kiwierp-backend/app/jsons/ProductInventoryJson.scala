package jsons

import models.ProductInventory
import play.api.libs.json.{JsValue, Json, Writes}

trait ProductInventoryJson extends KiwiERPJson {

  implicit val productInventoryWrites = new Writes[ProductInventory] {
    def writes(productInventory: ProductInventory): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(productInventory.createdAt),
      "description" -> productInventory.description,
      "id" -> productInventory.id,
      "product_id" -> productInventory.productId,
      "quantity" -> productInventory.quantity,
      "status" -> productInventory.status,
      "updatedAt" -> dateTimeToString(productInventory.updatedAt)
    )
  }

}
