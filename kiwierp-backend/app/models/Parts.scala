package models

import models.daos.PartsDAO
import org.joda.time.DateTime

case class Parts
(id: Long,
 productId: Long,
 name: String,
 description: Option[String] = None,
 neededQuantity: Int,
 unclassifiedQuantity: Int,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None,
 inventories: Seq[Inventory] = Nil,
 inventoryOrders: Seq[InventoryOrder] = Nil) {

  def this(parts: Parts) = this(
    parts.id,
    parts.productId,
    parts.name,
    parts.description,
    parts.neededQuantity,
    parts.unclassifiedQuantity,
    parts.createdAt,
    parts.updatedAt,
    parts.deletedAt,
    parts.inventories,
    parts.inventoryOrders
  )

}

object Parts extends PartsDAO
