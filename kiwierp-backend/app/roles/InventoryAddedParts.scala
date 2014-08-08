package roles

import models.{Inventory, Parts}
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait InventoryAddedParts {

  this: Parts =>

  def addInventory(description: Option[String], quantity: Int)(implicit s: AsyncDBSession): Future[Inventory] =
    Inventory.create(id, description, quantity)

}
