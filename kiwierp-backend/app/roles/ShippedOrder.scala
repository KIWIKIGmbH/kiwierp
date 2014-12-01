package roles

import models.Order
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait ShippedOrder {

  this: Order =>

  def checkStatus() = if (status != "ordered") throw new InvalidRequest

  def shipped(shippedDate: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Order.save(id)(shippedDate = Option(shippedDate), status = "shipped")

}
