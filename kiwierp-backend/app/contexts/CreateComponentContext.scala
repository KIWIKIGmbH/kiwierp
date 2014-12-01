package contexts

import models.{Component, Product}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.ComponentAddedProduct
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class CreateComponentContext private
(product: Product,
 name: String,
 description: Option[String],
 neededQuantity: Int)(implicit s: AsyncDBSession) {

  private def create(): Future[Component] = {
    val componentAddedProduct = new Product(product) with ComponentAddedProduct

    componentAddedProduct.addComponent(name, description, neededQuantity)
  }

}

object CreateComponentContext extends KiwiERPContext {

  def apply(productId: Long,
            name: String,
            description: Option[String],
            neededQuantity: Int): Future[Component] = AsyncDB withPool { implicit s =>
    Product.find(productId) flatMap { product =>
      val cxt = new CreateComponentContext(product, name, description, neededQuantity)

      cxt.create()
    } recover handleNotFound(new InvalidRequest)
  }

}
