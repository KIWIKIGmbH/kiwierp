package contexts

import models.{Product, ProductInventory}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDB

import scala.concurrent.Future

object CreateProductInventoryContext extends KiwiERPContext {

  def apply(productId: Long,
            description: Option[String],
            status: String,
            quantity: Int): Future[ProductInventory] =
    AsyncDB.withPool { implicit s =>
      Product.find(productId) flatMap { product =>
        ProductInventory.create(productId, description, status, quantity)
      }
    }

}
