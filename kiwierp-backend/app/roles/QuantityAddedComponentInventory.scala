package roles

import models.ComponentInventory
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait QuantityAddedComponentInventory {

  this: ComponentInventory =>

  def store(addedQuantity: Int)(implicit s: AsyncDBSession): Future[Int] =
    ComponentInventory.updateQuantity(id)(quantity + addedQuantity)

}
