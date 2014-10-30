package roles

import models.Inventory
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedInventory {

  this: Inventory =>

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Inventory.destroy(id, deletedAt)

}
