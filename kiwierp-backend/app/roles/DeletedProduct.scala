package roles

import models.{Inventory, InventoryField, Parts, Product}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedProduct {

  this: Product =>

  def deleteInventoryField(deletedInventoryField: InventoryField with DeletedInventoryField, deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    deletedInventoryField.deleteInventoryFieldValues(deletedAt) flatMap (_ => deletedInventoryField.deleted(deletedAt))

  def deleteInventory(deletedParts: Parts with DeletedParts,
                      deletedInventory: Inventory with DeletedInventory,
                      deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    deletedParts.deleteInventory(deletedInventory, deletedAt)

  def deleteParts(deletedParts: Parts with DeletedParts, deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    deletedParts.deleted(deletedAt)

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] = Product.destroy(id, deletedAt)

}
