package contexts

import models.{Component, Product}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDB
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

object CreateComponentContext extends KiwiERPContext {

  def apply(productId: Long,
            name: String,
            description: Option[String],
            neededQuantity: Int): Future[Component] = AsyncDB withPool { implicit s =>
    Product.find(productId) flatMap { _ =>
      Component.create(productId, name, description, neededQuantity)
    } recover handleNotFound(new InvalidRequest)
  }

}
