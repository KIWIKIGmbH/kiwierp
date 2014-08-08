package roles

import models.{InventoryOrder, Parts, Supplier}
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait OrderReceivedSupplier {

  this: Supplier =>

  def receiveOrder(orderedParts: Parts with OrderedParts, orderedNum: Int, orderedDate: DateTime)(implicit s: AsyncDBSession): Future[InventoryOrder] = {
    val INITIAL_STATUS = "ordered"

    InventoryOrder.create(orderedParts.id, id, orderedNum, orderedDate, INITIAL_STATUS)
  }

}
