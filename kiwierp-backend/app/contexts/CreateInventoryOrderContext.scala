package contexts

import models.{InventoryOrder, Parts, Supplier}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{OrderReceivedSupplier, OrderedParts}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class CreateInventoryOrderContext private (parts: Parts, supplier: Supplier, quantity: Int, orderedDate: DateTime)(implicit s: AsyncDBSession) {

  private def create(): Future[InventoryOrder] = {
    val orderedParts = new Parts(parts) with OrderedParts
    val orderReceivedSupplier = new Supplier(supplier) with OrderReceivedSupplier

    orderReceivedSupplier.receiveOrder(orderedParts, quantity, orderedDate)
  }

}

object CreateInventoryOrderContext extends KiwiERPContext {

  def apply(partsId: Long, supplierId: Long, quantity: Int, orderedDate: DateTime): Future[InventoryOrder] =
    AsyncDB withPool { implicit s =>
      Parts.find(partsId) recover handleNotFound(new InvalidRequest) flatMap { parts =>
        Supplier.find(supplierId) recover handleNotFound(new InvalidRequest) flatMap { supplier =>
          new CreateInventoryOrderContext(parts, supplier, quantity, orderedDate).create()
        }
      }
    }

}
