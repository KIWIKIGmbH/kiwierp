package roles

import models.{Inventory, Parts}
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedParts {

  this: Parts =>

  def deleteInventory(deletedInventory: Inventory with DeletedInventory, deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    deletedInventory.deleted(deletedAt)

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] = Parts.destroy(id, deletedAt)

}
