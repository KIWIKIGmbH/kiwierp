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

  private val (co, i, io) = (Component.co, Inventory.i, Order.io)

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

  def findWithComponentsAndInventoriesAndInventoryOrders
  (id: Long)(implicit s: ADS = AsyncDB.sharedSession): Future[Product] = withSQL {
    selectFrom[Product](Product as pr)
      .leftJoin(Component as co).on(
        sqls
          .eq(co.productId, pr.id)
          .and.isNull(co.deletedAt)
      )
      .leftJoin(Inventory as i).on(
        sqls
          .eq(i.componentId, co.id)
          .and.isNull(i.deletedAt)
      )
      .leftJoin(Order as io).on(
        sqls
          .eq(io.componentId, co.id)
          .and.isNull(io.deletedAt)
      )
      .where.eq(pr.id, id)
      .and.append(isNotDeleted)
  }.one(apply(pr)).toManies(Component.opt(co), Inventory.opt(i), Order.opt(io)).map { (product, components, inventories, orders) =>
    product.copy(
      components = components map { component =>
        component.copy(
          inventories = inventories filter (_.componentId == component.id),
          orders = orders filter (_.componentId == component.id)
        )
      }
    )
  }.single().future map getOrNotFound

  def findWithComponents(id: Long)(implicit s: ADS = AsyncDB.sharedSession): Future[Product] =
    withSQL {
      selectFrom[Product](Product as pr)
        .leftJoin(Component as co).on(
          sqls
            .eq(co.productId, pr.id)
            .and.isNull(co.deletedAt)
        )
        .where.eq(pr.id, id)
        .and.append(isNotDeleted)
    }.one(apply(pr)).toMany(Component.opt(co)).map { (product, components) =>
      product.copy(components = components)
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
