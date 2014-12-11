package roles

import models.{Component, Order}
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeliveredComponent {

  this: Component =>

  def archive(deliveredInventoryOrder: Order with DeliveredOrder)
             (implicit s: AsyncDBSession): Future[Int] =
    Component.updateUnclassifiedQuantity(id)(deliveredInventoryOrder.quantity + unclassifiedQuantity)

}
