package roles

import models.{Inventory, Parts}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait ClassifiedParts {

  this: Parts =>

  def checkUnclassifiedQuantity(classifiedQuantity: Int): Unit =
    if (classifiedQuantity > unclassifiedQuantity) throw new InvalidRequest

  def classified(quantityAddedInventory: Inventory with QuantityAddedInventory, classifiedQuantity: Int)(implicit s: AsyncDBSession): Future[Int] =
    classified(classifiedQuantity) flatMap { _ =>
      quantityAddedInventory.store(classifiedQuantity)
    }

  def classifiedAndAddInventory(classifiedQuantity: Int, inventoryDescription: Option[String])(implicit s: AsyncDBSession): Future[Inventory] =
    classified(classifiedQuantity) flatMap { _ =>
      Inventory.create(id, inventoryDescription, classifiedQuantity)
    }

  private def classified(classifiedQuantity: Int)(implicit s: AsyncDBSession): Future[Int] =
    Parts.updateUnclassifiedQuantity(id)(unclassifiedQuantity - classifiedQuantity)

}
