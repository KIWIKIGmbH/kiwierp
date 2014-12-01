package models

import models.daos.ProductDAO
import org.joda.time.DateTime

case class Product
(id: Long,
 name: String,
 description: Option[String] = None,
 createdAt: DateTime,
 updatedAt: DateTime,
 deletedAt: Option[DateTime] = None,
 components: Seq[Component] = Nil) {

  def this(product: Product) = this(
    product.id,
    product.name,
    product.description,
    product.createdAt,
    product.updatedAt,
    product.deletedAt,
    product.components
  )

}

object Product extends ProductDAO
