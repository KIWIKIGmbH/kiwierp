package controllers

import contexts.{AuthenticationContext, CreateUserContext, ReadUserContext}
import jsons.UserJson
import models.User
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

object UsersController extends KiwiERPController with UserJson {

  def list = AuthorizedAction.async { implicit req =>
    Page(User.findAll) map { results =>
      val (users, page) = results
      val json = Json.obj(
        "count" -> users.size,
        "page" -> page,
        "results" -> Json.toJson(users)
      )

      Ok(json)
    }
  }

  def create = AuthorizedAction.async(parse.json) { implicit req =>
    case class CreateForm(name: String, password: String, userType: String)

    implicit val createReads: Reads[CreateForm] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](60)) and
      (__ \ 'password).read[String](minLength[String](1) keepAnd maxLength[String](60)) and
      (__ \ 'userType).read[String](minLength[String](1) keepAnd maxLength[String](8))
    )(CreateForm.apply _)

    req.body.validate[CreateForm].fold(
      valid = { j =>
        CreateUserContext(j.name, j.password, j.userType, req.accessToken.user) map { user =>
          CreatedWithLocation(Json.toJson(user))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def read(id: Long) = AuthorizedAction.async { req =>
    ReadUserContext(id, req.accessToken.user) map { user =>
      Ok(Json.toJson(user))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    case class UpdateForm(name: String, userType: String)

    implicit val updateReads: Reads[UpdateForm] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](60)) and
      (__ \ 'userType).read[String](minLength[String](1) keepAnd maxLength[String](8))
    )(UpdateForm.apply _)

    req.body.validate[UpdateForm].fold(
      valid = { j =>
        User.save(id)(j.name, j.userType) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def delete(id: Long) = AuthorizedAction.async {
    User.destroy(id) map (_ => NoContent)
  }

  def authenticate = KiwiERPAction.async(parse.urlFormEncoded) { implicit req =>
    case class AuthenticateForm(name: String, password: String)

    val form = Form(
      mapping(
        "name" -> nonEmptyText(minLength = 1, maxLength = 60),
        "password" -> nonEmptyText(minLength = 1, maxLength = 64)
      )(AuthenticateForm.apply)(AuthenticateForm.unapply))

    form.bindFromRequest.fold(
      success = { f =>
        AuthenticationContext(f.name, f.password) map { accessToken =>
          Ok(Json.toJson(accessToken))
        }
      },
      hasErrors = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

}
