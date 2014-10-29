package models

import models.daos.SupplierDAO
import org.joda.time.DateTime

case class Supplier
(id: Long,
 companyName: String,
 personalName: String,
 phoneNumber: String,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None) {

  def this(supplier: Supplier) = this(
    supplier.id,
    supplier.companyName,
    supplier.personalName,
    supplier.phoneNumber,
    supplier.createdAt,
    supplier.updatedAt,
    supplier.deletedAt
  )

}

object Supplier extends SupplierDAO
