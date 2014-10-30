package roles

import models.{Inventory, Parts}
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedParts {

  this: Parts =>

  def deleteInventories(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Inventory.destroyAllByPartsId(id, deletedAt)

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Parts.destroy(id, deletedAt)

}
