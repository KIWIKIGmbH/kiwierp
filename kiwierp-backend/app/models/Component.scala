package models

import models.daos.ComponentDAO
import org.joda.time.DateTime

case class Component
(id: Long,
 productId: Long,
 name: String,
 description: Option[String] = None,
 neededQuantity: Int,
 unclassifiedQuantity: Int,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None,
 inventories: Seq[ComponentInventory] = Nil,
 orders: Seq[Order] = Nil) {

  def this(component: Component) = this(
    component.id,
    component.productId,
    component.name,
    component.description,
    component.neededQuantity,
    component.unclassifiedQuantity,
    component.createdAt,
    component.updatedAt,
    component.deletedAt,
    component.inventories,
    component.orders
  )

}

object Component extends ComponentDAO
