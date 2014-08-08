package models.daos

import models.InventoryFieldValue
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait InventoryFieldValueDAO extends KiwiERPDAO[InventoryFieldValue] {

  override val tableName = "inventory_field_values"

  override val columnNames = Seq(
    "inventory_id",
    "inventory_field_id",
    "value",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(ifv: ResultName[InventoryFieldValue])(rs: WRS): InventoryFieldValue =
    InventoryFieldValue(
      rs.long(ifv.inventoryId),
      rs.long(ifv.inventoryFieldId),
      rs.string(ifv.value),
      rs.jodaDateTime(ifv.createdAt),
      rs.jodaDateTime(ifv.updatedAt),
      rs.jodaDateTimeOpt(ifv.deletedAt)
    )

  lazy val s = syntax("ifv")

  val ifv = s

  def create(inventoryId: Long, inventoryFieldId: Long, value: String)(implicit s: ADS = AsyncDB.sharedSession): Future[InventoryFieldValue] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFuture {
      insertInto(InventoryFieldValue)
        .namedValues(
          column.inventoryId -> inventoryId,
          column.inventoryFieldId -> inventoryFieldId,
          column.value -> value,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
    } map { _ =>
      InventoryFieldValue(inventoryId, inventoryFieldId, value, createdAt, updatedAt)
    }
  }

  def destroyAllByInventoryId(inventoryId: Long, deletedAt: DateTime = DateTime.now)(implicit s: ADS = AsyncDB.sharedSession): Future[Int] =
    updateFuture {
      update(InventoryFieldValue)
        .set(
          column.deletedAt -> deletedAt
        )
        .where.eq(column.inventoryId, inventoryId)
        .and.isNull(column.deletedAt)
    }

  def destroyAllByInventoryFieldId(inventoryFieldId: Long, deletedAt: DateTime = DateTime.now)(implicit s: ADS = AsyncDB.sharedSession): Future[Int] =
    updateFuture {
      update(InventoryFieldValue)
        .set(
          column.deletedAt -> deletedAt
        )
        .where.eq(column.inventoryFieldId, inventoryFieldId)
        .and.isNull(column.deletedAt)
    }

}
