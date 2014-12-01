package models.daos

import models.{Inventory, Component}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait ComponentDAO extends KiwiERPDAO[Component] {

  override val tableName = "components"

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

  def apply(co: ResultName[Component])(rs: WRS): Component =
    Component(
      rs.long(co.id),
      rs.long(co.productId),
      rs.string(co.name),
      rs.stringOpt(co.description),
      rs.int(co.neededQuantity),
      rs.int(co.unclassifiedQuantity),
      rs.jodaDateTime(co.createdAt),
      rs.jodaDateTime(co.updatedAt),
      rs.jodaDateTimeOpt(co.deletedAt)
    )

  lazy val s = syntax("co")

  val co = s

  private val i = Inventory.i

  def create(productId: Long,
             name: String,
             description: Option[String],
             neededQuantity: Int)(implicit s: ADS = AsyncDB.sharedSession): Future[Component] = {
    val NOTHING_UNCLASSIFIED = 0
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(Component)
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
      Component(id, productId, name, description, neededQuantity, NOTHING_UNCLASSIFIED, createdAt, updatedAt)
    }
  }

  def findAllByProductId(productId: Long)
                        (page: Int = DEFAULT_PAGE)
                        (implicit s: ADS = AsyncDB.sharedSession): Future[List[Component]] = withSQL {
    selectFrom(Component as co)
      .where.eq(co.productId, productId)
      .and.append(isNotDeleted)
      .orderBy(co.id)
      .limit(DEFAULT_LIMIT)
      .offset((page - 1) * DEFAULT_LIMIT)
  } mapListFuture apply(co)

  def save(id: Long)
          (name: String, description: Option[String], neededQuantity: Int)
          (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(Component)
      .set(
        column.name -> name,
        column.description -> description,
        column.neededQuantity -> neededQuantity,
        column.updatedAt -> DateTime.now
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

  def updateUnclassifiedQuantity(id: Long)
                                (unclassifiedQuantity: Int)
                                (implicit s: ADS = AsyncDB.sharedSession): Future[Int] =
    updateFuture {
      update(Component)
        .set(
          column.unclassifiedQuantity -> unclassifiedQuantity
        )
        .where.eq(column.id, id)
        .and.isNull(column.deletedAt)
    }

  def destroyAllByProductId(productId: Long, deletedAt: DateTime = DateTime.now)
                           (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFuture {
    update(Component)
      .set(
        column.deletedAt -> deletedAt
      )
      .where.eq(column.productId, productId)
      .and.isNull(column.deletedAt)
  }

}
