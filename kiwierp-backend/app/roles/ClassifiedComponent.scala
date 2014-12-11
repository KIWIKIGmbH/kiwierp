package roles

import models.{Component, ComponentInventory}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait ClassifiedComponent {

  this: Component =>

  def checkUnclassifiedQuantity(classifiedQuantity: Int) =
    if (classifiedQuantity > unclassifiedQuantity) throw new InvalidRequest

  def classified(quantityAddedInventory: ComponentInventory with QuantityAddedComponentInventory,
                 classifiedQuantity: Int)(implicit s: AsyncDBSession): Future[Int] =
    classified(classifiedQuantity) flatMap { _ =>
      quantityAddedInventory.store(classifiedQuantity)
    }

  def classifiedAndAddInventory(classifiedQuantity: Int,
                                inventoryDescription: Option[String])
                               (implicit s: AsyncDBSession): Future[ComponentInventory] =
    classified(classifiedQuantity) flatMap { _ =>
      ComponentInventory.create(id, inventoryDescription, classifiedQuantity)
    }

  private def classified(classifiedQuantity: Int)(implicit s: AsyncDBSession): Future[Int] =
    Component.updateUnclassifiedQuantity(id)(unclassifiedQuantity - classifiedQuantity)

}
