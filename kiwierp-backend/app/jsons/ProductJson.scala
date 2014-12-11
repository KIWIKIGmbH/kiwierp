package jsons

import models.Product
import play.api.libs.json.{JsValue, Json, Writes}

trait ProductJson extends KiwiERPJson with ComponentJson with ProductInventoryJson {

  implicit val productWrites = new Writes[Product] {
    def writes(product: Product): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(product.createdAt),
      "description" -> product.description,
      "id" -> product.id,
      "name" -> product.name,
      "updatedAt" -> dateTimeToString(product.updatedAt),
      "components" -> Json.toJson(product.components),
      "inventories" -> Json.toJson(product.inventories)
    )
  }

}
