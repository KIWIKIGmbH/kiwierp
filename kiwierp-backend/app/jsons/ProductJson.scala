package jsons

import models.Product
import play.api.libs.json.{JsValue, Json, Writes}

trait ProductJson extends KiwiERPJson with PartsJson {

  implicit val productWrites = new Writes[Product] {
    def writes(product: Product): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(product.createdAt),
      "description" -> product.description,
      "id" -> product.id,
      "name" -> product.name,
      "updatedAt" -> dateTimeToString(product.updatedAt),
      "partsList" -> Json.toJson(product.partsSeq)
    )
  }

}
