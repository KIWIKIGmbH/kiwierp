package models.daos

import models.User
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait UserDAO extends KiwiERPDAO[User] {

  override val tableName = "users"

  override val columnNames = Seq(
    "id",
    "name",
    "password",
    "user_type",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(u: ResultName[User])(rs: WRS): User = User(
    rs.long(u.id),
    rs.string(u.name),
    rs.string(u.password),
    rs.string(u.userType),
    rs.jodaDateTime(u.createdAt),
    rs.jodaDateTime(u.updatedAt),
    rs.jodaDateTimeOpt(u.deletedAt)
  )

  lazy val s = syntax("u")

  val u = s

  def create(name: String, password: String, userType: String)
            (implicit s: ADS = AsyncDB.sharedSession): Future[User] = {
    val createdAt = DateTime.now
    val updatedAt = createdAt

    updateFutureAndReturnGeneratedKey {
      insertInto(User)
        .namedValues(
          column.name -> name,
          column.password -> password,
          column.userType -> userType,
          column.createdAt -> createdAt,
          column.updatedAt -> updatedAt
        )
        .returningId
    } map { id =>
      User(id, name, password, userType, createdAt, updatedAt)
    }
  }

  def save(id: Long)
          (name: String, userType: String)
          (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(User)
      .set(
        column.name -> name,
        column.userType -> userType,
        column.updatedAt -> DateTime.now
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

  def findByName(name: String)(implicit s: ADS = AsyncDB.sharedSession): Future[Option[User]] =
    withSQL {
      selectFrom(User as u)
        .where.eq(u.name, name)
        .and.append(isNotDeleted)
    }.map(apply(u)).single().future

}
