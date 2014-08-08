package contexts

import models.{InventoryOrder, Parts}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{DeliveredInventoryOrder, DeliveredParts, ShippedInventoryOrder}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class UpdateInventoryOrderContext private (inventoryOrder: InventoryOrder)(implicit s: AsyncDBSession) {

  private def ship(shippedDate: DateTime): Future[Int] = {
    val shippedInventoryOrder = new InventoryOrder(inventoryOrder) with ShippedInventoryOrder

    shippedInventoryOrder.checkStatus()
    shippedInventoryOrder.shipped(shippedDate)
  }

  private def deliver(parts: Parts, deliveredDate: DateTime): Future[Int] = {
    val deliveredInventoryOrder = new InventoryOrder(inventoryOrder) with DeliveredInventoryOrder
    val deliveredParts = new Parts(parts) with DeliveredParts

    deliveredInventoryOrder.checkStatus()
    deliveredInventoryOrder.delivered(deliveredDate) flatMap { _ =>
      deliveredParts.archive(deliveredInventoryOrder)
    }
  }

}

object UpdateInventoryOrderContext extends KiwiERPContext {

  def apply(id: Long, status: String, statusChangedDate: DateTime): Future[Int] = status match {
    case "shipped" => AsyncDB withPool { implicit s =>
      InventoryOrder.find(id) flatMap { inventoryOrder =>
        new UpdateInventoryOrderContext(inventoryOrder).ship(statusChangedDate)
      }
    }
    case "delivered" => AsyncDB localTx { implicit tx =>
      InventoryOrder.findWithParts(id) flatMap { inventoryOrder =>
        val parts = inventoryOrder.parts.get

        new UpdateInventoryOrderContext(inventoryOrder).deliver(parts, statusChangedDate)
      }
    }
    case _ => throw new InvalidRequest
  }

}
