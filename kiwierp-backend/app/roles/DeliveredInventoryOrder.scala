package roles

import models.InventoryOrder
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait DeliveredInventoryOrder {

  this: InventoryOrder =>

  def checkStatus(): Unit = if (status != "shipped") throw new InvalidRequest

  def delivered(deliveredDate: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    InventoryOrder.save(id)(shippedDate, Option(deliveredDate), "delivered")

}
