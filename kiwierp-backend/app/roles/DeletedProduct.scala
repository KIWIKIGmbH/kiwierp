package roles

import models.{Inventory, Parts, Product}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait DeletedProduct {

  this: Product =>

  def deletePartsSeq(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] = {
    val partsIds = partsSeq.map(_.id)

    if (partsIds.nonEmpty) {
      Inventory.destroyAllByPartsIds(partsSeq.map(_.id), deletedAt) flatMap { _ =>
        Parts.destroyAllByProductId(id, deletedAt)
      }
    } else {
      Future.successful(1)
    }
  }

  def deleted(deletedAt: DateTime)(implicit s: AsyncDBSession): Future[Int] =
    Product.destroy(id, deletedAt)

}
