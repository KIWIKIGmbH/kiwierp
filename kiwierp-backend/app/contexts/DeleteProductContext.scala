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

    deletedProduct.deletePartsSeq(deletedAt) flatMap { _ =>
      deletedProduct.deleted(deletedAt)
    }
  }

}

object DeleteProductContext {

  def apply(id: Long): Future[Int] = AsyncDB localTx { implicit tx =>
    Product.findWithPartsSeq(id) flatMap { product =>
      new DeleteProductContext(product).delete()
    }
  }

}
