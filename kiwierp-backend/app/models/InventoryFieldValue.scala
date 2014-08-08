package models

import models.daos.InventoryFieldValueDAO
import org.joda.time.DateTime

case class InventoryFieldValue
(inventoryId: Long,
 inventoryFieldId: Long,
 value: String,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None) {

  def this(inventoryFieldValue: InventoryFieldValue) = this(
    inventoryFieldValue.inventoryId,
    inventoryFieldValue.inventoryFieldId,
    inventoryFieldValue.value,
    inventoryFieldValue.createdAt,
    inventoryFieldValue.updatedAt,
    inventoryFieldValue.deletedAt
  )

}

object InventoryFieldValue extends InventoryFieldValueDAO