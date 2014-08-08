package contexts

import models.{Inventory, InventoryConsumption, Parts, Product}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{ConsumedInventory, ConsumedParts, ManufacturedProduct}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class ManufactureProductContext private
(product: Product,
 partsSeq: Seq[Parts],
 consumedNum: Int,
 inventoryIds: Map[Long, Int])(implicit tx: AsyncDBSession) {

  private def manufacture(): Future[InventoryConsumption] = {
    val manufacturedProduct = new Product(product) with ManufacturedProduct

    val consumedNumSeq = partsSeq map { parts =>
      val consumedParts = new Parts(parts) with ConsumedParts

      val consumedInventoriesNumSeq = consumedParts.inventories map { inventory =>
        val consumedInventory = new Inventory(inventory) with ConsumedInventory
        val consumedQuantity = inventoryIds.get(consumedInventory.id).get

        manufacturedProduct.consumeParts(consumedParts, consumedInventory, consumedQuantity)
      }

      Future.reduce(consumedInventoriesNumSeq)(_ + _) map { actualConsumedNum =>
        consumedParts.checkPartsConsumption(actualConsumedNum, consumedNum)
      }
    }

    Future.sequence(consumedNumSeq) flatMap (_ => manufacturedProduct.manufacture(consumedNum))
  }
}

object ManufactureProductContext extends KiwiERPContext {

  def apply(id: Long, consumedNum: Int, partsIds: List[Long], inventoryIds: Map[Long, Int]): Future[InventoryConsumption] =
    AsyncDB localTx { implicit tx =>
      Product.findWithPartsAndInventoriesFromIds(id, partsIds, inventoryIds.keys.toList) flatMap { product =>
        val partsSeq = product.partsSeq
        val inventories = partsSeq.map(_.inventories).flatten

        val isValidPartsIds = partsSeq.size == partsIds.size
        val isValidInventoryIds = inventories.size == inventoryIds.size
        val isValidIds = isValidPartsIds && isValidInventoryIds

        if (isValidIds) new ManufactureProductContext(product, partsSeq, consumedNum, inventoryIds).manufacture()
        else throw new InvalidRequest
      }
    }

}