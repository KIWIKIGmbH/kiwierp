package contexts

import models.{Product, ProductInventory}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDB

import scala.concurrent.Future

object ReadProductInventoryContext {

  def apply(productId: Long, inventoryId: Long): Future[ProductInventory] =
    AsyncDB.withPool { implicit s =>
      Product.find(productId) flatMap { _ =>
        ProductInventory.find(inventoryId)
      }
    }

}
