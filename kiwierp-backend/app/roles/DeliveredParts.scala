package roles

import models.{InventoryOrder, Parts}
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeliveredParts {

  this: Parts =>

  def archive(deliveredInventoryOrder: InventoryOrder with DeliveredInventoryOrder)(implicit s: AsyncDBSession): Future[Int] =
    Parts.updateUnclassifiedQuantity(id)(deliveredInventoryOrder.quantity + unclassifiedQuantity)

}
