package controllers

import contexts.{AuthenticationContext, CreateUserContext, ReadUserContext}
import jsons.UserJson
import models.User
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object UsersController extends KiwiERPController {

  def index = AuthorizedAction.async { implicit req =>
    Page(User.findAll) map { results =>
      val (users, page) = results

      Ok(UserJson.index(users, page))
    }
  }

  def create = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class CreateForm(name: String, password: String, userType: String)

    val form = Form(
      mapping(
        "name" -> nonEmptyText(minLength = 1, maxLength = 60),
        "password" -> nonEmptyText(minLength = 1, maxLength = 64),
        "userType" -> nonEmptyText(minLength = 1, maxLength = 8)
      )(CreateForm.apply)(CreateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      CreateUserContext(f.name, f.password, f.userType, req.accessToken.user) map { user =>
        CreatedWithLocation(UserJson.create(user))
      }
    }
  }

  def read(id: Long) = AuthorizedAction.async { req =>
    ReadUserContext(id, req.accessToken.user) map { user =>
      Ok(UserJson.read(user))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class UpdateForm(name: String, userType: String)

    val form = Form(
      mapping(
        "name" -> nonEmptyText(minLength = 1, maxLength = 60),
        "userType" -> nonEmptyText(minLength = 1, maxLength = 10)
      )(UpdateForm.apply)(UpdateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      User.save(id)(f.name, f.userType) map (_ => NoContent)
    }
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

    form.bindFromRequestAndCheckErrors { f =>
      AuthenticationContext(f.name, f.password) map { accessToken =>
        Ok(UserJson.authenticate(accessToken))
      }
    }
  }

}
