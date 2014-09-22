package controllers

import contexts.AuthorizationContext
import models.AccessToken
import play.api.Play.current
import play.api.data.Form
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import play.api.libs.json.JsValue
import play.api.mvc._
import utils.exceptions._

import scala.concurrent.Future

trait KiwiERPController extends Controller {

  val DATETIME_PATTERN = "yyyy-MM-dd HH:mm"

  val MAX_NUMBER = 2147483647

  val MAX_LONG_NUMBER = MAX_NUMBER

  class AuthorizedRequest[A]
  (val accessToken: AccessToken, private val req: Request[A]) extends WrappedRequest(req)

  object KiwiERPAction {

    def async[A](p: BodyParser[A])(block: Request[A] => Future[Result]): Action[A] = Action.async(p) { req =>
      val TIMEOUT_MILL_SECONDS = 30000
      val timeoutError = Promise.timeout(throw new TemporarilyUnavailable, TIMEOUT_MILL_SECONDS)
      val handleException: PartialFunction[Throwable, Result] = {
        case e: KiwiERPException => KiwiERPError.result(e)
      }

      Future.firstCompletedOf(Seq(block(req), timeoutError)) recover handleException
    }

  }

  object AuthorizedAction {

    def async[A](p: BodyParser[A])(block: AuthorizedRequest[A] => Future[Result]): Action[A] = KiwiERPAction.async(p) { req =>
      val authorize: String => Future[AccessToken] = token => AuthorizationContext(token, req.method, req.path)
      val result: AccessToken => Future[Result] = accessToken => block(new AuthorizedRequest(accessToken, req))
      val err = KiwiERPError.futureResult(new InvalidRequest)

      req.getQueryString("token") map (authorize(_) flatMap result) getOrElse err
    }

    def async(block: AuthorizedRequest[AnyContent] => Future[Result]): Action[AnyContent] = async(parse.anyContent)(block)

    def async(block: => Future[Result]): Action[AnyContent] = async(_ => block)

  }

  def isNum(numStr: String): Boolean = numStr matches "^[1-9]\\d*$"

  def isId(idStr: String): Boolean = isNum(idStr) && idStr.toLong < MAX_LONG_NUMBER

  implicit class BindFromRequestAndCheckErrors[T](self: Form[T]) {

    def bindFromRequestAndCheckErrors(success: T => Future[Result])(implicit req: Request[Map[String, Seq[String]]]): Future[Result] = {
      val hasErrors: Form[T] => Future[Result] = ef => KiwiERPError.futureResult(new InvalidRequest)

      self.bindFromRequest.fold(hasErrors, success)
    }

  }

  override val TODO = AuthorizedAction.async { _ => KiwiERPError.futureResult(new APINotImplemented) }

  def CreatedWithLocation(json: JsValue, path: Option[String] = None)(implicit req: AuthorizedRequest[Map[String, Seq[String]]]): Result = {
    val appContext = current.configuration.getString("application.context").get
    val host = req.host
    val resource = path map (appContext + _) getOrElse req.path
    val id = json \ "id"
    val location = s"$host$resource/$id"

    Created(json).withHeaders(LOCATION -> location)
  }

  object Page {

    def apply[M](searchFunc: Int => Future[List[M]])(implicit req: AuthorizedRequest[AnyContent]): Future[(List[M], Int)] = {
      val page = req.getQueryString("page") filter isNum map (_.toInt) getOrElse 1

      searchFunc(page) map (result => result -> page)
    }

  }

}
