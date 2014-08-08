package models

import models.daos.InventoryDAO
import org.joda.time.DateTime

case class Inventory
(id: Long,
 partsId: Long,
 description: Option[String],
 quantity: Int,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None,
 parts: Option[Parts] = None) {

  def this(inventory: Inventory) = this(
    inventory.id,
    inventory.partsId,
    inventory.description,
    inventory.quantity,
    inventory.createdAt,
    inventory.updatedAt,
    inventory.deletedAt,
    inventory.parts
  )

}

object Inventory extends InventoryDAO