package contexts

import models.{Component, ComponentInventory}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDB

import scala.concurrent.Future

object ReadComponentInventoryContext {

  def apply(componentId: Long, inventoryId: Long): Future[ComponentInventory] =
    AsyncDB.withPool { implicit s =>
      Component.find(componentId) flatMap { _ =>
        ComponentInventory.find(inventoryId)
      }
    }

}
