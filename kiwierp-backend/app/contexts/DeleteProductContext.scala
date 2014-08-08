package contexts

import models.{Inventory, Parts, Product}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{DeletedInventory, DeletedParts, DeletedProduct}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}

import scala.concurrent.Future

class DeleteProductContext private (product: Product, deletedAt: DateTime = DateTime.now)(implicit tx: AsyncDBSession) {

  private def delete(): Future[Int] = {
    val deletedProduct = new Product(product) with DeletedProduct

    val deletedPartsNumSeq = deletedProduct.partsSeq map { parts =>
      val deletedParts = new Parts(parts) with DeletedParts
      val deletedInventoriesNumSeq = deletedParts.inventories map { inventory =>
        val deletedInventory = new Inventory(inventory) with DeletedInventory

        deletedProduct.deleteInventory(deletedParts, deletedInventory, deletedAt)
      }

      Future.sequence(deletedInventoriesNumSeq) flatMap (_ => deletedProduct.deleteParts(deletedParts, deletedAt))
    }

    Future.sequence(deletedPartsNumSeq) flatMap { _ =>
      deletedProduct.deleted(deletedAt)
    }
  }

}

object DeleteProductContext {

  def apply(id: Long): Future[Int] = AsyncDB localTx { implicit tx =>
    Product.findWithPartsAndInventories(id) flatMap { product =>
      new DeleteProductContext(product).delete()
    }
  }

}
