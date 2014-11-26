package controllers

import com.wordnik.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import contexts.AuthenticationContext
import jsons.SessionJson
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import utils.exceptions.InvalidRequest

@Api(
  value = "/sessions",
  description = "Authentication API"
)
object SessionsController extends KiwiERPController with SessionJson {

  @ApiOperation(
    nickname = "sessions",
    value = "Authenticate user",
    notes = "",
    response = classOf[models.apidocs.AccessToken],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "name",
        value = "User's name",
        required = true,
        paramType = "form",
        dataType = "String"
      ),
      new ApiImplicitParam(
        name = "password",
        value = "User's password",
        required = true,
        paramType = "form",
        dataType = "String"
      )
    )
  )
  def index = KiwiERPAction.async(parse.urlFormEncoded) { implicit req =>
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
