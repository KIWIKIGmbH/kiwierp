package models.daos

import models.{AccessToken, User}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._

import scala.concurrent.Future

trait AccessTokenDAO extends KiwiERPDAO[AccessToken] {

  override val tableName = "access_tokens"

  override val columnNames = Seq(
    "token",
    "user_id",
    "expires_in",
    "token_type",
    "created_at"
  )

  def apply(a: ResultName[AccessToken])(rs: WRS): AccessToken =
    AccessToken(
      rs.string(a.token),
      rs.long(a.userId),
      rs.int(a.expiresIn),
      rs.string(a.tokenType),
      rs.jodaDateTime(a.createdAt)
    )

  def apply(a: SyntaxProvider[AccessToken], u: SyntaxProvider[User])(rs: WRS): AccessToken =
    apply(a)(rs).copy(user = User.opt(u)(rs))

  lazy val s = syntax("a")

  val a = s

  private val u = User.u

  def create(token: String, userId: Long, expiresIn: Int, tokenType: String)(implicit s: ADS = AsyncDB.sharedSession): Future[AccessToken] = {
    val createdAt = DateTime.now

    updateFuture {
      insertInto(AccessToken)
        .namedValues(
          column.token -> token,
          column.userId -> userId,
          column.expiresIn -> expiresIn,
          column.tokenType -> tokenType,
          column.createdAt -> createdAt
        )
    } map { _ =>
      AccessToken(token, userId, expiresIn, tokenType, createdAt)
    }
  }

  def findWithUserByToken(token: String)(implicit s: ADS = AsyncDB.sharedSession): Future[AccessToken] = withSQL {
    selectFrom[AccessToken](AccessToken as a)
      .innerJoin(User as u).on(
        sqls
          .eq(a.userId, u.id)
          .and.isNull(u.deletedAt))
      .where.eq(a.token, token)
  } mapSingleFuture apply(a, u)

  def destroyByUserId(userId: Long)(implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFuture {
    deleteFrom(AccessToken)
      .where.eq(column.userId, userId)
  }

}