package jsons

import models.Component
import play.api.libs.json.{JsValue, Json, Writes}

trait ComponentJson extends KiwiERPJson with InventoryJson with OrderJson {

  implicit val componentWrites = new Writes[Component] {
    def writes(component: Component): JsValue = Json.obj(
      "createdAt" -> dateTimeToString(component.createdAt),
      "description" -> component.description,
      "id" -> component.id,
      "name" -> component.name,
      "neededQuantity" -> component.neededQuantity,
      "productId" -> component.productId,
      "unclassifiedQuantity" -> component.unclassifiedQuantity,
      "updatedAt" -> dateTimeToString(component.updatedAt),
      "inventories" -> Json.toJson(component.inventories),
      "orders" -> Json.toJson(component.orders)
    )
  }

}
