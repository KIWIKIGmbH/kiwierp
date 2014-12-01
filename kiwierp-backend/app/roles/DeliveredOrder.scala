package roles

import models.Order
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait DeliveredOrder {

  this: Order =>

  def checkStatus(): Unit = if (status != "shipped") throw new InvalidRequest

  def delivered(deliveredDate: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Order.save(id)(shippedDate, Option(deliveredDate), "delivered")

}
