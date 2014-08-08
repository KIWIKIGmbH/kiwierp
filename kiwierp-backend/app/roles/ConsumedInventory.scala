package roles

import models.Inventory
import scalikejdbc.async.AsyncDBSession
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait ConsumedInventory {

  this: Inventory =>

  def consume(consumedQuantity: Int)(implicit s: AsyncDBSession): Future[Int] =
    if (quantity < consumedQuantity) throw new InvalidRequest
    else Inventory.updateQuantity(id)(quantity - consumedQuantity)



}
