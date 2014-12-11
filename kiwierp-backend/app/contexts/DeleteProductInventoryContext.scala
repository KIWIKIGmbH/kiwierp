package contexts

import models.{ProductInventory, Product}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDB

import scala.concurrent.Future

object DeleteProductInventoryContext {

  def apply(productId: Long, inventoryId: Long): Future[Int] = AsyncDB.withPool { implicit s =>
    Product.find(productId) flatMap { _ =>
      ProductInventory.find(inventoryId) flatMap { _ =>
        ProductInventory.destroy(inventoryId)
      }
    }
  }

}
