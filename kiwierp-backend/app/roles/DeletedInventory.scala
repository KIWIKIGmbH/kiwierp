package roles

import models.{Inventory, InventoryFieldValue}
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedInventory {

  this: Inventory =>

  def deleteInventoryFieldValues(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    InventoryFieldValue.destroyAllByInventoryId(id, deletedAt)

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] = Inventory.destroy(id, deletedAt)

}
