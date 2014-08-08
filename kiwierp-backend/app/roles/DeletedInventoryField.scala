package roles

import models.{InventoryField, InventoryFieldValue}
import org.joda.time.DateTime
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedInventoryField {

  this: InventoryField =>

  def deleteInventoryFieldValues(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    InventoryFieldValue.destroyAllByInventoryFieldId(id, deletedAt)

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] = InventoryFieldValue.destroy(id, deletedAt)

}
