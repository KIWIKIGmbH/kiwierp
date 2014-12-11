package models

import models.daos.ProductInventoryDAO
import org.joda.time.DateTime

case class ProductInventory
(id: Long,
 productId: Long,
 description: Option[String],
 status: String,
 quantity: Int,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None)

object ProductInventory extends ProductInventoryDAO
