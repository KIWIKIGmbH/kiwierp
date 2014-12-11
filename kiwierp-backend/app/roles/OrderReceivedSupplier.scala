package roles

import models.{Component, Order, Supplier}
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait OrderReceivedSupplier {

  this: Supplier =>

  def receiveOrder(orderedComponent: Component with OrderedComponent,
                   quantity: Int, orderedDate: DateTime)
                  (implicit s: AsyncDBSession): Future[Order] = {
    val INITIAL_STATUS = "ordered"

    Order.create(orderedComponent.id, id, quantity, orderedDate, INITIAL_STATUS)
  }

}
