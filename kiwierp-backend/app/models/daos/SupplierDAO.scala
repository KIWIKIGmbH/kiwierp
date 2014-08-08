package models.daos

import models.Supplier
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait SupplierDAO extends KiwiERPDAO[Supplier] {

  override val tableName = "suppliers"

  override val columnNames = Seq(
    "id",
    "company_name",
    "personal_name",
    "phone_number",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(su: ResultName[Supplier])(rs: WRS): Supplier = Supplier(
    rs.long(su.id),
    rs.string(su.companyName),
    rs.string(su.personalName),
    rs.string(su.phoneNumber),
    rs.jodaDateTime(su.createdAt),
    rs.jodaDateTime(su.updatedAt),
    rs.jodaDateTimeOpt(su.deletedAt)
  )

  lazy val s = syntax("su")

  val su = s

  def create(companyName: String, personalName: String, phoneNumber: String)(implicit session: ADS = AsyncDB.sharedSession): Future[Supplier] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(Supplier)
        .namedValues(
          column.companyName -> companyName,
          column.personalName -> personalName,
          column.phoneNumber -> phoneNumber,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      Supplier(id, companyName, personalName, phoneNumber, createdAt, updatedAt)
    }
  }

  def save(id: Long)(companyName: String, personalName: String, phoneNumber: String)(implicit s: ADS = AsyncDB.sharedSession): Future[Int] =
    updateFutureOrNotFound {
      update(Supplier)
        .set(
          column.companyName -> companyName,
          column.personalName -> personalName,
          column.phoneNumber -> phoneNumber,
          column.updatedAt -> DateTime.now
        )
        .where.eq(column.id, id)
        .and.isNull(column.deletedAt)
    }

}
