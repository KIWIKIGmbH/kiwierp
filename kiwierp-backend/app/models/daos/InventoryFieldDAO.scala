package models.daos

import models.{InventoryField, InventoryFieldValue, Product}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait InventoryFieldDAO extends KiwiERPDAO[InventoryField] {

  override val tableName = "inventory_fields"

  override val columnNames = Seq(
    "id",
    "product_id",
    "name",
    "field_type",
    "is_required",
    "min",
    "max",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(ifd: ResultName[InventoryField])(rs: WRS): InventoryField =
    InventoryField(
      rs.long(ifd.id),
      rs.long(ifd.productId),
      rs.string(ifd.name),
      rs.string(ifd.fieldType),
      rs.boolean(ifd.isRequired),
      rs.intOpt(ifd.min),
      rs.intOpt(ifd.max),
      rs.jodaDateTime(ifd.createdAt),
      rs.jodaDateTime(ifd.updatedAt),
      rs.jodaDateTimeOpt(ifd.deletedAt)
    )

  def apply(ifd: SyntaxProvider[InventoryField], pr: SyntaxProvider[Product])(rs: WRS): InventoryField =
    apply(ifd)(rs).copy(product = Product.opt(pr)(rs))

  lazy val s = syntax("ifd")

  val ifd = s

  private val (ifv, pr) = (InventoryFieldValue.ifv, Product.pr)

  def findAllByProductId(productId: Long)(page: Int = DEFAULT_PAGE)(implicit s: ADS = AsyncDB.sharedSession): Future[List[InventoryField]] =
    withSQL {
      selectFrom(InventoryField as ifd)
        .where.eq(ifd.productId, productId)
        .and.append(isNotDeleted)
        .orderBy(ifd.id)
        .limit(DEFAULT_LIMIT)
        .offset((page - 1) * DEFAULT_LIMIT)
    } mapListFuture apply(ifd)

  def create(productId: Long,
             name: String,
             fieldType: String,
             isRequired: Boolean,
             min: Option[Int] = None,
             max: Option[Int] = None)(implicit s: ADS = AsyncDB.sharedSession): Future[InventoryField] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(InventoryField)
        .namedValues(
          column.productId -> productId,
          column.name -> name,
          column.fieldType -> fieldType,
          column.isRequired -> isRequired,
          column.min -> min,
          column.max -> max,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      InventoryField(id, productId, name, fieldType, isRequired, min, max, createdAt, updatedAt)
    }
  }

  def findWithInventoryFieldValues(id: Long)(implicit s: ADS = AsyncDB.sharedSession): Future[InventoryField] =
    withSQL {
      selectFrom[InventoryField](InventoryField as ifd)
        .leftJoin(InventoryFieldValue as ifv).on(
          sqls
            .eq(ifv.inventoryFieldId, ifd.id)
            .and.isNull(ifv.deletedAt)
        )
        .where.eq(ifd.id, id)
        .and.append(isNotDeleted)
    }.one(apply(ifd)).toMany(InventoryFieldValue.opt(ifv)).map { (inventoryField, inventoryFieldValues) =>
      inventoryField.copy(inventoryFieldValues = inventoryFieldValues)
    }.single().future map getOrNotFound

  def save(id: Long)
          (name: String, fieldType: String, isRequired: Boolean, min: Option[Int], max: Option[Int])
          (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(InventoryField)
      .set(
        column.name -> name,
        column.fieldType -> fieldType,
        column.isRequired -> isRequired,
        column.min -> min,
        column.max -> max,
        column.updatedAt -> DateTime.now
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

}
