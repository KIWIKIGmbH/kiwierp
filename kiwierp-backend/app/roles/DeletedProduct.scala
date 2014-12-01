package roles

import models.{Inventory, Component, Product}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedProduct {

  this: Product =>

  def deleteComponents(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] = {
    val componentIds = components.map(_.id)

    if (componentIds.nonEmpty) {
      Inventory.destroyAllByComponentIds(components.map(_.id), deletedAt) flatMap { _ =>
        Component.destroyAllByProductId(id, deletedAt)
      }
    } else {
      Future.successful(1)
    }
  }

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Product.destroy(id, deletedAt)

}
