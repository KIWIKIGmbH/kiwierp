package contexts

import models.{Component, ComponentInventory}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDB

import scala.concurrent.Future

object UpdateComponentInventoryContext {

  def apply(componentId: Long,
            inventoryId: Long,
            description: Option[String],
            quantity: Int): Future[Int] = AsyncDB.withPool { implicit s =>
    Component.find(componentId) flatMap { _ =>
      ComponentInventory.find(inventoryId) flatMap { _ =>
        ComponentInventory.save(inventoryId: Long)(description, quantity)
      }
    }
  }


}
