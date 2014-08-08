package jsons

import models.Product
import play.api.libs.json.Json

object ProductJson extends KiwiERPJson[Product] {

  def base(product: Product) = Json.obj(
    "createdAt" -> product.createdAt,
    "description" -> product.description,
    "id" -> product.id,
    "name" -> product.name,
    "updatedAt" -> product.updatedAt
  )

  override def read(product: Product) = base(product) ++ Json.obj(
    "partsList" -> product.partsSeq.map { parts =>
      PartsJson.base(parts) ++ Json.obj(
        "inventories" -> parts.inventories.map { inventory =>
          InventoryJson.base(inventory)
        },
        "inventoryOrders" -> parts.inventoryOrders.map { inventoryOrder =>
          InventoryOrderJson.base(inventoryOrder)
        }
      )
    }
  )

}
