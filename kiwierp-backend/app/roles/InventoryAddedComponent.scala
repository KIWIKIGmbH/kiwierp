package roles

import models.{Inventory, Component}
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait InventoryAddedComponent {

  this: Component =>

  def addInventory(description: Option[String], quantity: Int)
                  (implicit s: AsyncDBSession): Future[Inventory] =
    Inventory.create(id, description, quantity)

}
