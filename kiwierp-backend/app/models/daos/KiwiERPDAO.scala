package models.daos

import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scalikejdbc._
import scalikejdbc.async._
import utils.exceptions.ResourceNotFound

import scala.concurrent.Future

trait KiwiERPDAO[A] extends SQLSyntaxSupport[A] {

  val DEFAULT_LIMIT = 20

  val DEFAULT_PAGE = 1

  val s: QuerySQLSyntaxProvider[SQLSyntaxSupport[A], A]

  lazy val isNotDeleted = sqls.isNull(s.deletedAt)

  type ADS = AsyncDBSession

  type WRS = WrappedResultSet

  def apply(a: ResultName[A])(rs: WRS): A

  def apply(a: SyntaxProvider[A])(rs: WRS): A = apply(a.resultName)(rs)

  def opt(a: SyntaxProvider[A])(rs: WRS): Option[A] = rs.longOpt(a.resultName.id) map { _ =>
    apply(a.resultName)(rs)
  }

  def getOrNotFound(optA: Option[A]): A = optA getOrElse (throw new ResourceNotFound)

  def findAll(page: Int = DEFAULT_PAGE)
             (implicit session: ADS = AsyncDB.sharedSession): Future[List[A]] = withSQL {
    selectFrom(as(s))
      .where.append(isNotDeleted)
      .orderBy(s.id)
      .limit(DEFAULT_LIMIT)
      .offset((page - 1) * DEFAULT_LIMIT)
  } mapListFuture apply(s)

  def find(id: Long)(implicit session: ADS = AsyncDB.sharedSession): Future[A] = withSQL {
    selectFrom(as(s))
      .where.eq(s.id, id)
      .and.append(isNotDeleted)
  } mapSingleFuture apply(s)

  def destroy(id: Long, deletedAt: DateTime = DateTime.now)
             (implicit s: ADS = AsyncDB.sharedSession): Future[Int] = updateFutureOrNotFound {
    update(this)
      .set(
        column.deletedAt -> deletedAt
      )
      .where.eq(column.id, id)
      .and.isNull(column.deletedAt)
  }

  implicit class SQLFuture(self: SQL[_, NoExtractor]) {

    def mapSingleFuture(f: WRS => A)(implicit session: ADS): Future[A] =
      self.map(f).single().future map getOrNotFound

    def mapListFuture(f: WRS => A)(implicit session: ADS): Future[List[A]] =
      self.map(f).list().future

  }

  object updateFutureAndReturnGeneratedKey {

    def apply(sql: => SQLBuilder[_])(implicit session: ADS): Future[Long] =
      withSQL(sql).updateAndReturnGeneratedKey().future

  }

  object updateFutureOrNotFound {

    def apply(sql: => SQLBuilder[_])(implicit session: ADS): Future[Int] =
      updateFuture(sql) map { updatedNum =>
        val SUCCESS = 1
        val FAILURE = 0

        updatedNum match {
          case SUCCESS => updatedNum
          case FAILURE => throw new ResourceNotFound
        }
      }

  }

}
