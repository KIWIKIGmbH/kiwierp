package contexts

import models.InventoryField
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.DeletedInventoryField
import scalikejdbc.async.{AsyncDB, AsyncDBSession}

import scala.concurrent.Future

class DeleteInventoryFieldContext private (inventoryField: InventoryField, deletedAt: DateTime = DateTime.now)(implicit tx: AsyncDBSession) {

  private def delete(): Future[Int] = {
    val deletedInventoryField = new InventoryField(inventoryField) with DeletedInventoryField

    deletedInventoryField.deleteInventoryFieldValues(deletedAt) flatMap { _ =>
      deletedInventoryField.deleted(deletedAt)
    }
  }

}

object DeleteInventoryFieldContext {

  def apply(id: Long): Future[Int] = AsyncDB localTx { implicit tx =>
    InventoryField.find(id) flatMap { inventoryField =>
      new DeleteInventoryFieldContext(inventoryField).delete()
    }
  }

}
