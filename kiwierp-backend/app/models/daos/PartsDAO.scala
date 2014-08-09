package models.daos

import models.{Inventory, Parts}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait PartsDAO extends KiwiERPDAO[Parts] {

  override val tableName = "parts"

  override val columnNames = Seq(
    "id",
    "product_id",
    "name",
    "description",
    "needed_quantity",
    "unclassified_quantity",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(pa: ResultName[Parts])(rs: WRS): Parts =
    Parts(
      rs.long(pa.id),
      rs.long(pa.productId),
      rs.string(pa.name),
      rs.stringOpt(pa.description),
      rs.int(pa.neededQuantity),
      rs.int(pa.unclassifiedQuantity),
      rs.jodaDateTime(pa.createdAt),
      rs.jodaDateTime(pa.updatedAt),
      rs.jodaDateTimeOpt(pa.deletedAt)
    )

  lazy val s = syntax("pa")

  val pa = s

  private val i = Inventory.i

  def create(productId: Long, name: String, description: Option[String], neededQuantity: Int)(implicit s: ADS = AsyncDB.sharedSession): Future[Parts] = {
    val NOTHING_UNCLASSIFIED = 0
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(Parts)
        .namedValues(
          column.productId -> productId,
          column.name -> name,
          column.description -> description,
          column.neededQuantity -> neededQuantity,
          column.unclassifiedQuantity -> NOTHING_UNCLASSIFIED,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      Parts(id, productId, name, description, neededQuantity, NOTHING_UNCLASSIFIED, createdAt, updatedAt)
    }
  }

  def findAllByProductId(productId: Long)(page: Int = DEFAULT_PAGE)
                        (implicit s: ADS = AsyncDB.sharedSession): Future[List[Parts]] = withSQL {
    selectFrom(Parts as pa)
      .where.eq(pa.productId, productId)
      .and.append(isNotDeleted)
      .orderBy(pa.id)
      .limit(DEFAULT_LIMIT)
      .offset((page - 1) * DEFAULT_LIMIT)
  } mapListFuture apply(pa)

  def save(id: Long)(name: String, description: Option[String], neededQuantity: Int)
          (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(Parts)
      .set(
        column.name -> name,
        column.description -> description,
        column.neededQuantity -> neededQuantity,
        column.updatedAt -> DateTime.now
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

  def updateUnclassifiedQuantity(id: Long)(unclassifiedQuantity: Int)(implicit s: ADS = AsyncDB.sharedSession): Future[Int] =
    updateFuture {
      update(Parts)
        .set(
          column.unclassifiedQuantity -> unclassifiedQuantity
        )
        .where.eq(column.id, id)
        .and.isNull(column.deletedAt)
    }

  def destroyAllByProductId(productId: Long, deletedAt: DateTime = DateTime.now)(implicit s: ADS = AsyncDB.sharedSession): Future[Int] =
    updateFuture {
      update(Parts)
        .set(
          column.deletedAt -> deletedAt
        )
        .where.eq(column.productId, productId)
        .and.isNull(column.deletedAt)
    }

}
