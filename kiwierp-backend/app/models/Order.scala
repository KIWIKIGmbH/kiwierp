package models

import models.daos.OrderDAO
import org.joda.time.DateTime

case class Order
(id: Long,
 componentId: Long,
 supplierId: Long,
 quantity: Int,
 orderedDate: DateTime,
 shippedDate: Option[DateTime] = None,
 deliveredDate: Option[DateTime] = None,
 status: String,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None,
 component: Option[Component] = None) {

  def this(order: Order) = this(
    order.id,
    order.componentId,
    order.supplierId,
    order.quantity,
    order.orderedDate,
    order.shippedDate,
    order.deliveredDate,
    order.status,
    order.createdAt,
    order.updatedAt,
    order.deletedAt,
    order.component
  )

}

object Order extends OrderDAO
