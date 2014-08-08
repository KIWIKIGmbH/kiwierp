package models

import models.daos.InventoryFieldDAO
import org.joda.time.DateTime

case class InventoryField
(id: Long,
 productId: Long,
 name: String,
 fieldType: String,
 isRequired: Boolean,
 min: Option[Int],
 max: Option[Int],
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None,
 product: Option[Product] = None,
 inventoryFieldValues: Seq[InventoryFieldValue] = Nil) {

  def this(inventoryField: InventoryField) = this(
    inventoryField.id,
    inventoryField.productId,
    inventoryField.name,
    inventoryField.fieldType,
    inventoryField.isRequired,
    inventoryField.min,
    inventoryField.max,
    inventoryField.createdAt,
    inventoryField.updatedAt,
    inventoryField.deletedAt,
    inventoryField.product,
    inventoryField.inventoryFieldValues
  )

}

object InventoryField extends InventoryFieldDAO
