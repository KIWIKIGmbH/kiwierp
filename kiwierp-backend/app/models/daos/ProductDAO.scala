package models.daos

import models._
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait ProductDAO extends KiwiERPDAO[Product] {

  override val tableName = "products"

  override val columnNames = Seq(
    "id",
    "name",
    "description",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(pr: ResultName[Product])(rs: WRS): Product = Product(
    rs.long(pr.id),
    rs.string(pr.name),
    rs.stringOpt(pr.description),
    rs.jodaDateTime(pr.createdAt),
    rs.jodaDateTime(pr.updatedAt),
    rs.jodaDateTimeOpt(pr.deletedAt)
  )

  lazy val s = syntax("pr")

  val pr = s

  private val (pa, i, io) = (Parts.pa, Inventory.i, InventoryOrder.io)

  def create(name: String, description: Option[String])
            (implicit s: ADS = AsyncDB.sharedSession): Future[Product] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(Product)
        .namedValues(
          column.name -> name,
          column.description -> description,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      Product(id, name, description, createdAt, updatedAt)
    }
  }

  def findWithPartsAndInventoriesAndInventoryOrders
  (id: Long)(implicit s: ADS = AsyncDB.sharedSession): Future[Product] = withSQL {
    selectFrom[Product](Product as pr)
      .leftJoin(Parts as pa).on(
        sqls
          .eq(pa.productId, pr.id)
          .and.isNull(pa.deletedAt)
      )
      .leftJoin(Inventory as i).on(
        sqls
          .eq(i.partsId, pa.id)
          .and.isNull(i.deletedAt)
      )
      .leftJoin(InventoryOrder as io).on(
        sqls
          .eq(io.partsId, pa.id)
          .and.isNull(io.deletedAt)
      )
      .where.eq(pr.id, id)
      .and.append(isNotDeleted)
  }.one(apply(pr)).toManies(Parts.opt(pa), Inventory.opt(i), InventoryOrder.opt(io)).map { (product, partsSeq, inventories, inventoryOrders) =>
    product.copy(
      partsSeq = partsSeq map { parts =>
        parts.copy(
          inventories = inventories filter (_.partsId == parts.id),
          inventoryOrders = inventoryOrders filter (_.partsId == parts.id)
        )
      }
    )
  }.single().future map getOrNotFound

  def findWithPartsSeq(id: Long)(implicit s: ADS = AsyncDB.sharedSession): Future[Product] =
    withSQL {
      selectFrom[Product](Product as pr)
        .leftJoin(Parts as pa).on(
          sqls
            .eq(pa.productId, pr.id)
            .and.isNull(pa.deletedAt)
        )
        .where.eq(pr.id, id)
        .and.append(isNotDeleted)
    }.one(apply(pr)).toMany(Parts.opt(pa)).map { (product, partsSeq) =>
      product.copy(partsSeq = partsSeq)
    }.single().future map getOrNotFound

  def save(id: Long)
          (name: String, description: Option[String])
          (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(Product)
      .set(
        column.name -> name,
        column.description -> description,
        column.updatedAt -> DateTime.now
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

}
