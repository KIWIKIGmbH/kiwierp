package models.daos

import models.InventoryConsumption
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait InventoryConsumptionDAO extends KiwiERPDAO[InventoryConsumption] {

  override val tableName = "inventory_consumption"

  override val columns = Seq(
    "id",
    "product_id",
    "consumed",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(ic: ResultName[InventoryConsumption])(rs: WRS): InventoryConsumption =
    InventoryConsumption(
      rs.long(ic.id),
      rs.long(ic.productId),
      rs.int(ic.consumed),
      rs.jodaDateTime(ic.createdAt),
      rs.jodaDateTime(ic.updatedAt),
      rs.jodaDateTimeOpt(ic.deletedAt)
    )

  lazy val s = syntax("ic")

  val ic = s

  def create(productId: Long, consumed: Int)(implicit s: ADS = AsyncDB.sharedSession): Future[InventoryConsumption] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(InventoryConsumption)
        .namedValues(
          column.productId -> productId,
          column.consumed -> consumed
        )
        .returningId
    } map { id =>
      InventoryConsumption(id, productId, consumed, createdAt, updatedAt)
    }
  }

}
