package roles

import models.{Component, ComponentInventory, Product, ProductInventory}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedProduct {

  this: Product =>

  def deleteInventories(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] = {
    val inventoryIds = inventories.map(_.id)

    if (inventoryIds.nonEmpty) {
      ProductInventory.destroyAllByProductId(id, deletedAt)
    } else {
      Future.successful(1)
    }
  }

  def deleteComponents(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] = {
    val componentIds = components.map(_.id)

    if (componentIds.nonEmpty) {
      ComponentInventory.destroyAllByComponentIds(components.map(_.id), deletedAt) flatMap { _ =>
        Component.destroyAllByProductId(id, deletedAt)
      }
    } else {
      Future.successful(1)
    }
  }

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Product.destroy(id, deletedAt)

}
