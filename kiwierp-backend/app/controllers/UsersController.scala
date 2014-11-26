package controllers

import com.wordnik.swagger.annotations._
import contexts.{CreateUserContext, ReadUserContext}
import jsons.UserJson
import models.User
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

import scala.annotation.meta.field

case class UserCreationBody
(@(ApiModelProperty @field)(required = true) name: String,
 @(ApiModelProperty @field)(required = true) password: String,
 @(ApiModelProperty @field)(required = true) userType: String)

case class UserUpdateBody
(@(ApiModelProperty @field)(required = true) name: String,
 @(ApiModelProperty @field)(required = true) userType: String)

@Api(
  value = "/users",
  description = "CRUD and list (search) API of user"
)
object UsersController extends KiwiERPController with UserJson {

  @ApiOperation(
    nickname = "listUser",
    value = "find users",
    notes = "",
    response = classOf[models.apidocs.Users],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "page",
        value = "Page Number",
        required = false,
        paramType = "query",
        dataType = "Int"
      )
    )
  )
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

  @ApiOperation(
    nickname = "createUser",
    value = "Register user",
    notes = "",
    response = classOf[models.apidocs.User],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.UserCreationBody"
      )
    )
  )
  def create = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val createReads: Reads[UserCreationBody] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](60)) and
      (__ \ 'password).read[String](minLength[String](1) keepAnd maxLength[String](60)) and
      (__ \ 'userType).read[String](minLength[String](1) keepAnd maxLength[String](8))
    )(UserCreationBody.apply _)

    req.body.validate[UserCreationBody].fold(
      valid = { j =>
        CreateUserContext(j.name, j.password, j.userType, req.accessToken.user) map { user =>
          CreatedWithLocation(Json.toJson(user))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "readUser",
    value = "Find user by ID",
    notes = "",
    response = classOf[models.apidocs.User],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "User id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def read(id: Long) = AuthorizedAction.async { req =>
    ReadUserContext(id, req.accessToken.user) map { user =>
      Ok(Json.toJson(user))
    }
  }

  @ApiOperation(
    nickname = "updateUser",
    value = "Edit user",
    notes = "",
    response = classOf[Void],
    httpMethod = "PATCH"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "User id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.UserUpdateBody"
      )
    )
  )
  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val updateReads: Reads[UserUpdateBody] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](60)) and
      (__ \ 'userType).read[String](minLength[String](1) keepAnd maxLength[String](8))
    )(UserUpdateBody.apply _)

    req.body.validate[UserUpdateBody].fold(
      valid = { j =>
        User.save(id)(j.name, j.userType) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "deleteUser",
    value = "Remove user",
    notes = "",
    response = classOf[Void],
    httpMethod = "DELETE",
    consumes = "text/plain"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "User id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def delete(id: Long) = AuthorizedAction.async {
    User.destroy(id) map (_ => NoContent)
  }

}
