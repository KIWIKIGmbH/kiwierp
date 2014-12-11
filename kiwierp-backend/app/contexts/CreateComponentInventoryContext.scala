package contexts

import models.{Component, ComponentInventory}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDB

import scala.concurrent.Future

object CreateComponentInventoryContext extends KiwiERPContext {

  def apply(componentId: Long,
            description: Option[String],
            quantity: Int): Future[ComponentInventory] =
    AsyncDB withPool { implicit s =>
      Component.find(componentId) flatMap { _ =>
        ComponentInventory.create(componentId, description, quantity)
      }
    }

}
