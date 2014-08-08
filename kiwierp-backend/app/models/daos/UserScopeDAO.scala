package models.daos

import models.UserScope
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait UserScopeDAO extends KiwiERPDAO[UserScope] {

  override val tableName = "user_scopes"

  override val columnNames = Seq(
    "permitted_user_types",
    "method",
    "uri",
    "created_at",
    "updated_at",
    "deleted_at"
  )

  def apply(ut: ResultName[UserScope])(rs: WRS): UserScope = UserScope(
    rs.string(ut.method),
    rs.string(ut.uri),
    rs.jodaDateTime(ut.createdAt),
    rs.jodaDateTime(ut.updatedAt),
    rs.jodaDateTimeOpt(ut.deletedAt)
  )

  lazy val s = syntax("ut")

  val ut = s

  def findScope(userType: String, method: String, uri: String)(implicit s: ADS = AsyncDB.sharedSession): Future[UserScope] =
    withSQL {
      selectFrom(UserScope as ut)
        .where.append(sqls"$userType = ANY (ut.permitted_user_types)")
        .and.eq(ut.method, method)
        .and.eq(ut.uri, uri)
        .and.append(isNotDeleted)
    } mapSingleFuture apply(ut)

}
