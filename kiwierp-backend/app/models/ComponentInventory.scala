package models

import models.daos.ComponentInventoryDAO
import org.joda.time.DateTime

case class ComponentInventory
(id: Long,
 componentId: Long,
 description: Option[String],
 quantity: Int,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None,
 component: Option[Component] = None) {

  def this(inventory: ComponentInventory) = this(
    inventory.id,
    inventory.componentId,
    inventory.description,
    inventory.quantity,
    inventory.createdAt,
    inventory.updatedAt,
    inventory.deletedAt,
    inventory.component
  )

}

object ComponentInventory extends ComponentInventoryDAO
