package contexts

import models.{Parts, Product}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.PartsAddedProduct
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class CreatePartsContext private
(product: Product,
 name: String,
 description: Option[String],
 neededQuantity: Int)(implicit s: AsyncDBSession) {

  private def create(): Future[Parts] = {
    val partsAddedProduct = new Product(product) with PartsAddedProduct

    partsAddedProduct.addParts(name, description, neededQuantity)
  }

}

object CreatePartsContext extends KiwiERPContext {

  def apply(productId: Long,
            name: String,
            description: Option[String],
            neededQuantity: Int): Future[Parts] = AsyncDB withPool { implicit s =>
    Product.find(productId) flatMap { product =>
      val cxt = new CreatePartsContext(product, name, description, neededQuantity)

      cxt.create()
    } recover handleNotFound(new InvalidRequest)
  }

}
