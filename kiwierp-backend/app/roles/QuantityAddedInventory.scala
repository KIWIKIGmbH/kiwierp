package roles

import models.Inventory
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait QuantityAddedInventory {

  this: Inventory =>

  def store(addedQuantity: Int)(implicit s: AsyncDBSession): Future[Int] =
    Inventory.updateQuantity(id)(quantity + addedQuantity)

}
