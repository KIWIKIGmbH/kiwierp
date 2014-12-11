package models.daos

import models.{Product, ProductInventory}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait ProductInventoryDAO extends KiwiERPDAO[ProductInventory] {

  override val tableName = "product_inventories"

  override val columnNames = Seq(
    "id",
    "product_id",
    "description",
    "status",
    "quantity",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(pi: ResultName[ProductInventory])(rs: WRS): ProductInventory =
    ProductInventory(
      rs.long(pi.id),
      rs.long(pi.productId),
      rs.stringOpt(pi.description),
      rs.string(pi.status),
      rs.int(pi.quantity),
      rs.jodaDateTime(pi.createdAt),
      rs.jodaDateTime(pi.updatedAt),
      rs.jodaDateTimeOpt(pi.deletedAt)
    )

  lazy val s = syntax("pi")

  val pi = s

  private val pr = Product.pr

  def findAllByProductIdAndStatus
  (productId: Long, status: String)
  (page: Int = DEFAULT_PAGE)
  (implicit s: ADS = AsyncDB.sharedSession): Future[List[ProductInventory]] =
    withSQL {
      selectFrom(ProductInventory as pi)
        .where.eq(pi.productId, productId)
        .and.eq(pi.status, status)
        .and.append(isNotDeleted)
        .limit(DEFAULT_LIMIT)
        .offset((page - 1) * DEFAULT_PAGE)
    } mapListFuture apply(pi)

  def findAllByProductId(productId: Long)
                        (page: Int = DEFAULT_PAGE)
                        (implicit s: ADS = AsyncDB.sharedSession): Future[List[ProductInventory]] =
    withSQL {
      selectFrom(ProductInventory as pi)
        .where.eq(pi.productId, productId)
        .and.append(isNotDeleted)
        .limit(DEFAULT_LIMIT)
        .offset((page - 1) * DEFAULT_PAGE)
    } mapListFuture apply(pi)

  def create(productId: Long,
             description: Option[String],
             status: String,
             quantity: Int)(implicit s: ADS = AsyncDB.sharedSession): Future[ProductInventory] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(ProductInventory)
        .namedValues(
          column.productId -> productId,
          column.description -> description,
          column.status -> status,
          column.quantity -> quantity,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      ProductInventory(
        id = id,
        productId = productId,
        description = description,
        status = status,
        quantity = quantity,
        createdAt = createdAt,
        updatedAt = updatedAt
      )
    }
  }

  def save(id: Long)
          (description: Option[String], status: String, quantity: Int)
          (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(ProductInventory)
      .set(
        column.description -> description,
        column.status -> status,
        column.quantity -> quantity
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

  def destroyAllByProductId(productId: Long, deletedAt: DateTime = DateTime.now)
                           (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFuture {
    update(ProductInventory)
      .set(
        column.deletedAt -> deletedAt
      )
      .where.eq(column.productId, productId)
      .and.isNull(column.deletedAt)
  }

}
