package roles

import models.{Inventory, Parts}
import scalikejdbc.async.AsyncDBSession
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait ConsumedParts {

  this: Parts =>

  def consumeInventory(consumedInventory: Inventory with ConsumedInventory, consumedQuantity: Int)(implicit s: AsyncDBSession): Future[Int] =
    consumedInventory.consume(consumedQuantity)

  def checkPartsConsumption(actualConsumedNum: Int, consumedNum: Int): Unit =
    if (actualConsumedNum != consumedNum * neededQuantity) throw new InvalidRequest

}
