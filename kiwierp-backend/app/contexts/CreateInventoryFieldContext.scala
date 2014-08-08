package contexts

import models.{InventoryField, Product}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.InventoryFieldAddedProduct
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class CreateInventoryFieldContext private
(product: Product,
 name: String,
 fieldType: String,
 isRequired: Boolean,
 min: Option[Int],
 max: Option[Int])(implicit s: AsyncDBSession) {

  private def create(): Future[InventoryField] = {
    val inventoryFieldAddedProduct = new Product(product) with InventoryFieldAddedProduct

    inventoryFieldAddedProduct.addInventoryField(name, fieldType, isRequired, min, max)
  }

}

object CreateInventoryFieldContext extends KiwiERPContext {

  def apply(productId: Long, name: String, fieldType: String, isRequired: Boolean, min: Option[Int], max: Option[Int]): Future[InventoryField] =
    AsyncDB withPool { implicit s =>
      Product.find(productId) recover handleNotFound(new InvalidRequest) flatMap { product =>
        new CreateInventoryFieldContext(product, name, fieldType, isRequired, min, max).create()
      }
    }

}
