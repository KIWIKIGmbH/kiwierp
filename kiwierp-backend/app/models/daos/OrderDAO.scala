package models.daos

import models.{Order, Component}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait OrderDAO extends KiwiERPDAO[Order] {

  override val tableName = "orders"

  override val columnNames = Seq(
    "id",
    "component_id",
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

  def apply(io: ResultName[Order])(rs: WRS): Order =
    Order(
      rs.long(io.id),
      rs.long(io.componentId),
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

  def apply(io: SyntaxProvider[Order], pa: SyntaxProvider[Component])(rs: WRS): Order =
    apply(io)(rs).copy(component = Component.opt(pa)(rs))

  lazy val s = syntax("io")

  val io = s

  private val co = Component.co

  def findAllByComponentId(componentId: Long)
                          (page: Int = DEFAULT_PAGE)
                          (implicit s: ADS = AsyncDB.sharedSession): Future[List[Order]] =
    withSQL {
      selectFrom(Order as io)
        .where.eq(io.componentId, componentId)
        .and.append(isNotDeleted)
        .orderBy(io.id)
        .limit(DEFAULT_LIMIT)
        .offset((page - 1) * DEFAULT_LIMIT)
    } mapListFuture apply(io)

  def create(componentId: Long,
             supplierId: Long,
             quantity: Int,
             orderedDate: DateTime,
             status: String)(implicit s: ADS = AsyncDB.sharedSession): Future[Order] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(Order)
        .namedValues(
          column.componentId -> componentId,
          column.supplierId -> supplierId,
          column.quantity -> quantity,
          column.orderedDate -> orderedDate,
          column.status -> status,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      Order(
        id = id,
        componentId = componentId,
        supplierId = supplierId,
        quantity = quantity,
        orderedDate = orderedDate,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
      )
    }
  }

  def findWithComponent(id: Long)(implicit s: ADS = AsyncDB.sharedSession): Future[Order] =
    withSQL {
      selectFrom[Order](Order as io)
        .innerJoin(Component as co).on(
          sqls
            .eq(io.componentId, co.id)
            .and.isNull(co.deletedAt)
        )
        .where.eq(io.id, id)
        .and.append(isNotDeleted)
    } mapSingleFuture apply(io, co)

  def save(id: Long)
          (shippedDate: Option[DateTime] = None,
           deliveredDate: Option[DateTime] = None,
           status: String)
          (implicit session: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(Order)
      .set(
        column.shippedDate -> shippedDate,
        column.deliveredDate -> deliveredDate,
        column.status -> status
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

}
