package models

import models.daos.InventoryConsumptionDAO
import org.joda.time.DateTime

case class InventoryConsumption
(id: Long,
 productId: Long,
 consumed: Int,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None)

object InventoryConsumption extends InventoryConsumptionDAO
