package roles

import models.{Inventory, InventoryConsumption, Parts, Product}
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait ManufacturedProduct {

  this: Product =>

  def consumeParts(consumedParts: Parts with ConsumedParts,
                   consumedInventory: Inventory with ConsumedInventory,
                   consumedQuantity: Int)(implicit s: AsyncDBSession): Future[Int] =
    consumedParts.consumeInventory(consumedInventory, consumedQuantity)

  def manufacture(consumedNum: Int)(implicit s: AsyncDBSession): Future[InventoryConsumption] =
    InventoryConsumption.create(id, consumedNum)

}
