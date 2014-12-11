package contexts

import models.{Component, ComponentInventory}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDB

import scala.concurrent.Future

object DeleteComponentInventoryContext {

  def apply(componentId: Long, inventoryId: Long): Future[Int] = AsyncDB withPool { implicit s =>
    Component.find(componentId) flatMap { _ =>
      ComponentInventory.find(inventoryId) flatMap { inventory =>
        ComponentInventory.destroy(inventoryId)
      }
    }
  }

}
