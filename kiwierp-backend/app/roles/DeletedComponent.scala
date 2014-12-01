package roles

import models.{Inventory, Component}
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedComponent {

  this: Component =>

  def deleteInventories(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Inventory.destroyAllByComponentId(id, deletedAt)

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Component.destroy(id, deletedAt)

}
