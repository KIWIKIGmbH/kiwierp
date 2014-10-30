package contexts

import models.{Inventory, Parts}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{ClassifiedParts, QuantityAddedInventory}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}

import scala.concurrent.Future

class ClassifyPartsContext private
(parts: Parts,
 classifiedQuantity: Int)(implicit tx:AsyncDBSession) {

  private val classifiedParts = new Parts(parts) with ClassifiedParts

  classifiedParts.checkUnclassifiedQuantity(classifiedQuantity)

  private def classify(inventory: Inventory): Future[Int] = {
    val quantityAddedInventory = new Inventory(inventory) with QuantityAddedInventory

    classifiedParts.classified(quantityAddedInventory, classifiedQuantity)
  }

  private def classifyAndCreateInventory(inventoryDescription: Option[String]): Future[Inventory] =
    classifiedParts.classifiedAndAddInventory(classifiedQuantity, inventoryDescription)

}

object ClassifyPartsContext extends KiwiERPContext {

  def apply(id: Long, classifiedQuantity: Int, inventoryId: Long): Future[Int] =
    AsyncDB localTx { implicit tx =>
      Inventory.findWithParts(inventoryId, id) flatMap { inventory =>
        val parts = inventory.parts.get
        val cxt = new ClassifyPartsContext(parts, classifiedQuantity)

        cxt.classify(inventory)
      }
    }

  def apply(id: Long,
            classifiedQuantity: Int,
            inventoryDescription: Option[String]): Future[Inventory] =
    AsyncDB localTx { implicit tx =>
      Parts.find(id) flatMap { parts =>
        val cxt = new ClassifyPartsContext(parts, classifiedQuantity)

        cxt.classifyAndCreateInventory(inventoryDescription)
      }
    }

}
