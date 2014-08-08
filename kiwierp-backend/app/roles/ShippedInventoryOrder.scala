package roles

import models.InventoryOrder
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait ShippedInventoryOrder {

  this: InventoryOrder =>

  def checkStatus(): Unit = if (status != "ordered") throw new InvalidRequest

  def shipped(shippedDate: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    InventoryOrder.save(id)(shippedDate = Option(shippedDate), status = "shipped")

}
