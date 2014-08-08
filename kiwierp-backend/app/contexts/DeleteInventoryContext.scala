package contexts

import models.Inventory
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.DeletedInventory
import scalikejdbc.async.{AsyncDB, AsyncDBSession}

import scala.concurrent.Future

class DeleteInventoryContext private (inventory: Inventory, deletedAt: DateTime = DateTime.now)(implicit tx: AsyncDBSession) {

  private def delete(): Future[Int] = {
    val deletedInventory = new Inventory(inventory) with DeletedInventory

    deletedInventory.deleteInventoryFieldValues(deletedAt) flatMap (_ => deletedInventory.deleted(deletedAt))
  }

}

object DeleteInventoryContext {

  def apply(id: Long): Future[Int] = AsyncDB localTx { implicit tx =>
    Inventory.find(id) flatMap { inventory =>
      new DeleteInventoryContext(inventory).delete()
    }
  }

}
