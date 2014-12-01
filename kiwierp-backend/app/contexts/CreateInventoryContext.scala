package contexts

import models.{Inventory, Component}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.InventoryAddedComponent
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class CreateInventoryContext private
(component: Component,
 description: Option[String],
 quantity: Int)(implicit s: AsyncDBSession) {

  private def create(): Future[Inventory] = {
    val inventoryAddedComponent = new Component(component) with InventoryAddedComponent

    inventoryAddedComponent.addInventory(description, quantity)
  }

}

object CreateInventoryContext extends KiwiERPContext {

  def apply(componentId: Long, description: Option[String], quantity: Int): Future[Inventory] =
    AsyncDB withPool { implicit s =>
      Component.find(componentId) flatMap { component =>
        val cxt = new CreateInventoryContext(component, description, quantity)

        cxt.create()
      }
    } recover handleNotFound(new InvalidRequest)

}
