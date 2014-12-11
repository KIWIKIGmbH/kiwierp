package models.daos

import models.{Component, ComponentInventory}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait ComponentInventoryDAO extends KiwiERPDAO[ComponentInventory] {

  override val tableName = "component_inventories"

  override val columnNames = Seq(
    "id",
    "component_id",
    "description",
    "quantity",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(i: ResultName[ComponentInventory])(rs: WRS): ComponentInventory =
    ComponentInventory(
      rs.long(i.id),
      rs.long(i.componentId),
      rs.stringOpt(i.description),
      rs.int(i.quantity),
      rs.jodaDateTime(i.createdAt),
      rs.jodaDateTime(i.updatedAt),
      rs.jodaDateTimeOpt(i.deletedAt)
    )

  def apply(i: SyntaxProvider[ComponentInventory], co: SyntaxProvider[Component])(rs: WRS): ComponentInventory =
    apply(i)(rs).copy(component = Component.opt(co)(rs))

  lazy val s = syntax("i")

  val i = s

  private val co = Component.co

  def findAllByComponentId(componentId: Long)
                          (page: Int = DEFAULT_PAGE)
                          (implicit s: ADS = AsyncDB.sharedSession): Future[List[ComponentInventory]] = withSQL {
    selectFrom(ComponentInventory as i)
      .where.eq(i.componentId, componentId)
      .and.append(isNotDeleted)
      .orderBy(i.id)
      .limit(DEFAULT_LIMIT)
      .offset((page - 1) * DEFAULT_LIMIT)
  } mapListFuture apply(i)

  def create(componentId: Long,
             description: Option[String],
             quantity: Int)(implicit s: ADS = AsyncDB.sharedSession): Future[ComponentInventory] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(ComponentInventory)
        .namedValues(
          column.componentId -> componentId,
          column.description -> description,
          column.quantity -> quantity,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      ComponentInventory(id, componentId, description, quantity, createdAt, updatedAt)
    }
  }

  def findWithComponent(id: Long, componentId: Long)
                       (implicit s: ADS = AsyncDB.sharedSession): Future[ComponentInventory] = withSQL {
    selectFrom[ComponentInventory](ComponentInventory as i)
      .innerJoin(Component as co).on(
        sqls
          .eq(i.componentId, co.id)
          .and.isNull(co.deletedAt)
      )
      .where.eq(i.id, id)
      .and.eq(i.componentId, componentId)
  } mapSingleFuture apply(i, co)

  def save(id: Long)
          (description: Option[String], quantity: Int)
          (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(ComponentInventory)
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
    update(ComponentInventory)
      .set(
        column.quantity -> quantity
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

  def destroyAllByComponentId(componentId: Long, deletedAt: DateTime = DateTime.now)
                         (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFuture {
    update(ComponentInventory)
      .set(
        column.deletedAt -> deletedAt
      )
      .where.eq(column.componentId, componentId)
      .and.isNull(column.deletedAt)
  }

  def destroyAllByComponentIds(componentIds: Seq[Long], deletedAt: DateTime = DateTime.now)
                              (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFuture {
    update(ComponentInventory)
      .set(
        column.deletedAt -> deletedAt
      )
      .where.in(column.componentId, componentIds)
      .and.isNull(column.deletedAt)
  }

}
