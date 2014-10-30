package contexts

import models.{Inventory, Parts}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.InventoryAddedParts
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class CreateInventoryContext private
(parts: Parts,
 description: Option[String],
 quantity: Int)(implicit s: AsyncDBSession) {

  private def create(): Future[Inventory] = {
    val inventoryAddedParts = new Parts(parts) with InventoryAddedParts

    inventoryAddedParts.addInventory(description, quantity)
  }

}

object CreateInventoryContext extends KiwiERPContext {

  def apply(partsId: Long, description: Option[String], quantity: Int): Future[Inventory] =
    AsyncDB withPool { implicit s =>
      Parts.find(partsId) flatMap { parts =>
        val cxt = new CreateInventoryContext(parts, description, quantity)

        cxt.create()
      }
    } recover handleNotFound(new InvalidRequest)

}
