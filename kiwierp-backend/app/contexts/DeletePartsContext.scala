package contexts

import models.{Inventory, Parts}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{DeletedInventory, DeletedParts}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}

import scala.concurrent.Future

class DeletePartsContext private (parts: Parts, deletedAt: DateTime = DateTime.now)(implicit tx: AsyncDBSession) {

  private def delete(): Future[Int] = {
    val deletedParts = new Parts(parts) with DeletedParts

    val deletedInventoriesNumSeq = deletedParts.inventories map { inventory =>
      val deletedInventory = new Inventory(inventory) with DeletedInventory

      deletedParts.deleteInventory(deletedInventory, deletedAt)
    }

    Future.sequence(deletedInventoriesNumSeq) flatMap (_ => deletedParts.deleted(deletedAt))
  }

}

object DeletePartsContext {

  def apply(id: Long): Future[Int] = AsyncDB localTx { implicit tx =>
    Parts.findWithInventories(id) flatMap { parts =>
      new DeletePartsContext(parts).delete()
    }
  }

}
