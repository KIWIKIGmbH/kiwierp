package models.daos

import models.{InventoryOrder, Parts}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait InventoryOrderDAO extends KiwiERPDAO[InventoryOrder] {

  override val tableName = "inventory_orders"

  override val columnNames = Seq(
    "id",
    "parts_id",
    "supplier_id",
    "quantity",
    "ordered_date",
    "shipped_date",
    "delivered_date",
    "status",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(io: ResultName[InventoryOrder])(rs: WRS): InventoryOrder =
    InventoryOrder(
      rs.long(io.id),
      rs.long(io.partsId),
      rs.long(io.supplierId),
      rs.int(io.quantity),
      rs.jodaDateTime(io.orderedDate),
      rs.jodaDateTimeOpt(io.shippedDate),
      rs.jodaDateTimeOpt(io.deliveredDate),
      rs.string(io.status),
      rs.jodaDateTime(io.createdAt),
      rs.jodaDateTime(io.updatedAt),
      rs.jodaDateTimeOpt(io.deletedAt)
    )

  def apply(io: SyntaxProvider[InventoryOrder], pa: SyntaxProvider[Parts])(rs: WRS): InventoryOrder =
    apply(io)(rs).copy(parts = Parts.opt(pa)(rs))

  lazy val s = syntax("io")

  val io = s

  private val pa = Parts.pa

  def findAllByPartsId(partsId: Long)(page: Int = DEFAULT_PAGE)(implicit s: ADS = AsyncDB.sharedSession): Future[List[InventoryOrder]] =
    withSQL {
      selectFrom(InventoryOrder as io)
        .where.eq(io.partsId, partsId)
        .and.append(isNotDeleted)
        .orderBy(io.id)
        .limit(DEFAULT_LIMIT)
        .offset((page - 1) * DEFAULT_LIMIT)
    } mapListFuture apply(io)

  def create(partsId: Long, supplierId: Long, quantity: Int, orderedDate: DateTime, status: String)(implicit s: ADS = AsyncDB.sharedSession): Future[InventoryOrder] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(InventoryOrder)
        .namedValues(
          column.partsId -> partsId,
          column.supplierId -> supplierId,
          column.quantity -> quantity,
          column.orderedDate -> orderedDate,
          column.status -> status,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      InventoryOrder(
        id = id,
        partsId = partsId,
        supplierId = supplierId,
        quantity = quantity,
        orderedDate = orderedDate,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
      )
    }
  }

  def findWithParts(id: Long)(implicit s: ADS = AsyncDB.sharedSession): Future[InventoryOrder] = withSQL {
    selectFrom[InventoryOrder](InventoryOrder as io)
      .innerJoin(Parts as pa).on(
        sqls
          .eq(io.partsId, pa.id)
          .and.isNull(pa.deletedAt)
      )
      .where.eq(io.id, id)
      .and.append(isNotDeleted)
  } mapSingleFuture apply(io, pa)

  def save(id: Long)(shippedDate: Option[DateTime] = None, deliveredDate: Option[DateTime] = None, status: String)
          (implicit session: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(InventoryOrder)
      .set(
        column.shippedDate -> shippedDate,
        column.deliveredDate -> deliveredDate,
        column.status -> status
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

}