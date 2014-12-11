package contexts

import models.{ProductInventory, Product}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDB

import scala.concurrent.Future

object UpdateProductInventoryContext {

  def apply(productId: Long,
            inventoryId: Long,
            description: Option[String],
            status: String,
            quantity: Int): Future[Int] = AsyncDB.withPool { implicit s =>
    Product.find(productId) flatMap { _ =>
      ProductInventory.find(inventoryId) flatMap { _ =>
        ProductInventory.save(inventoryId)(description, status, quantity)
      }
    }
  }

}
