package models

import models.daos.InventoryOrderDAO
import org.joda.time.DateTime

case class InventoryOrder
(id: Long,
 partsId: Long,
 supplierId: Long,
 quantity: Int,
 orderedDate: DateTime,
 shippedDate: Option[DateTime] = None,
 deliveredDate: Option[DateTime] = None,
 status: String,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None,
 parts: Option[Parts] = None) {

  def this(inventoryOrder: InventoryOrder) = this(
    inventoryOrder.id,
    inventoryOrder.partsId,
    inventoryOrder.supplierId,
    inventoryOrder.quantity,
    inventoryOrder.orderedDate,
    inventoryOrder.shippedDate,
    inventoryOrder.deliveredDate,
    inventoryOrder.status,
    inventoryOrder.createdAt,
    inventoryOrder.updatedAt,
    inventoryOrder.deletedAt,
    inventoryOrder.parts
  )

}

object InventoryOrder extends InventoryOrderDAO