package contexts

import models.{Component, ComponentInventory}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{ClassifiedComponent, QuantityAddedComponentInventory}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}

import scala.concurrent.Future

class ClassifyComponentContext private
(Component: Component,
 classifiedQuantity: Int)(implicit tx:AsyncDBSession) {

  private val classifiedComponent = new Component(Component) with ClassifiedComponent

  classifiedComponent.checkUnclassifiedQuantity(classifiedQuantity)

  private def classify(inventory: ComponentInventory): Future[Int] = {
    val quantityAddedInventory = new ComponentInventory(inventory) with QuantityAddedComponentInventory

    classifiedComponent.classified(quantityAddedInventory, classifiedQuantity)
  }

  private def classifyAndCreateInventory(inventoryDescription: Option[String]): Future[ComponentInventory] =
    classifiedComponent.classifiedAndAddInventory(classifiedQuantity, inventoryDescription)

}

object ClassifyComponentContext extends KiwiERPContext {

  def apply(id: Long, classifiedQuantity: Int, inventoryId: Long): Future[Int] =
    AsyncDB localTx { implicit tx =>
      ComponentInventory.findWithComponent(inventoryId, id) flatMap { inventory =>
        val component = inventory.component.get
        val cxt = new ClassifyComponentContext(component, classifiedQuantity)

        cxt.classify(inventory)
      }
    }

  def apply(id: Long,
            classifiedQuantity: Int,
            inventoryDescription: Option[String]): Future[ComponentInventory] =
    AsyncDB localTx { implicit tx =>
      Component.find(id) flatMap { Component =>
        val cxt = new ClassifyComponentContext(Component, classifiedQuantity)

        cxt.classifyAndCreateInventory(inventoryDescription)
      }
    }

}
