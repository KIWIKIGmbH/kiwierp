package models.daos

import models.{Inventory, Parts}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait InventoryDAO extends KiwiERPDAO[Inventory] {

  override val tableName = "inventories"

  override val columnNames = Seq(
    "id",
    "parts_id",
    "description",
    "quantity",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(i: ResultName[Inventory])(rs: WRS): Inventory =
    Inventory(
      rs.long(i.id),
      rs.long(i.partsId),
      rs.stringOpt(i.description),
      rs.int(i.quantity),
      rs.jodaDateTime(i.createdAt),
      rs.jodaDateTime(i.updatedAt),
      rs.jodaDateTimeOpt(i.deletedAt)
    )

  def apply(i: SyntaxProvider[Inventory], pa: SyntaxProvider[Parts])(rs: WRS): Inventory =
    apply(i)(rs).copy(parts = Parts.opt(pa)(rs))

  lazy val s = syntax("i")

  val i = s

  private val pa = Parts.pa

  def findAllByPartsId(partsId: Long)
                      (page: Int = DEFAULT_PAGE)
                      (implicit s: ADS = AsyncDB.sharedSession): Future[List[Inventory]] = withSQL {
    selectFrom(Inventory as i)
      .where.eq(i.partsId, partsId)
      .and.append(isNotDeleted)
      .orderBy(i.id)
      .limit(DEFAULT_LIMIT)
      .offset((page - 1) * DEFAULT_LIMIT)
  } mapListFuture apply(i)

  def create(partsId: Long,
             description: Option[String],
             quantity: Int)(implicit s: ADS = AsyncDB.sharedSession): Future[Inventory] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(Inventory)
        .namedValues(
          column.partsId -> partsId,
          column.description -> description,
          column.quantity -> quantity,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      Inventory(id, partsId, description, quantity, createdAt, updatedAt)
    }
  }

  def findWithParts(id: Long, partsId: Long)
                   (implicit s: ADS = AsyncDB.sharedSession): Future[Inventory] = withSQL {
    selectFrom[Inventory](Inventory as i)
      .innerJoin(Parts as pa).on(
        sqls
          .eq(i.partsId, pa.id)
          .and.isNull(pa.deletedAt)
      )
      .where.eq(i.id, id)
      .and.eq(i.partsId, partsId)
  } mapSingleFuture apply(i, pa)

  def save(id: Long)
          (description: Option[String], quantity: Int)
          (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(Inventory)
      .set(
        column.description -> description,
        column.quantity -> quantity,
        column.updatedAt -> DateTime.now
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

  def updateQuantity(id: Long)
                    (quantity: Int)
                    (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFuture {
    update(Inventory)
      .set(
        column.quantity -> quantity
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

  def destroyAllByPartsId(partsId: Long, deletedAt: DateTime = DateTime.now)
                         (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFuture {
    update(Inventory)
      .set(
        column.deletedAt -> deletedAt
      )
      .where.eq(column.partsId, partsId)
      .and.isNull(column.deletedAt)
  }

  def destroyAllByPartsIds(partsIds: Seq[Long], deletedAt: DateTime = DateTime.now)
                          (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFuture {
    update(Inventory)
      .set(
        column.deletedAt -> deletedAt
      )
      .where.in(column.partsId, partsIds)
      .and.isNull(column.deletedAt)
  }

}
