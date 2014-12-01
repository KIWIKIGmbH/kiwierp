package controllers

import com.wordnik.swagger.annotations._
import jsons.SupplierJson
import models.Supplier
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

import scala.annotation.meta.field

case class SupplierCreationBody
(@(ApiModelProperty @field)(required = true) companyName: String,
 @(ApiModelProperty @field)(required = true) personalName: String,
 @(ApiModelProperty @field)(required = true) phoneNumber: String)

case class SupplierUpdateBody
(@(ApiModelProperty @field)(required = true) companyName: String,
 @(ApiModelProperty @field)(required = true) personalName: String,
 @(ApiModelProperty @field)(required = true) phoneNumber: String)

@Api(
  value = "/inventory-management/suppliers",
  description = "CRUD and list (search) API of supplier"
)
object SuppliersController extends KiwiERPController with SupplierJson {

  @ApiOperation(
    nickname = "listSupplier",
    value = "find suppliers",
    notes = "",
    response = classOf[models.apidocs.Suppliers],
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
    Page(Supplier.findAll) map { results =>
      val (suppliers, page) = results
      val json = Json.obj(
        "count" -> suppliers.size,
        "page" -> page,
        "results" -> Json.toJson(suppliers)
      )

      Ok(json)
    }
  }

  @ApiOperation(
    nickname = "createSupplier",
    value = "Register supplier",
    notes = "",
    response = classOf[models.apidocs.Supplier],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.SupplierCreationBody"
      )
    )
  )
  def create = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val createReads: Reads[SupplierCreationBody] = (
      (__ \ 'companyName).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'personalName).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'phoneNumber).read[String](minLength[String](1) keepAnd maxLength[String](120))
    )(SupplierCreationBody.apply _)

    req.body.validate[SupplierCreationBody].fold(
      valid = { j =>
        Supplier.create(j.companyName, j.personalName, j.phoneNumber) map { supplier =>
          CreatedWithLocation(Json.toJson(supplier))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "readSupplier",
    value = "Find supplier by ID",
    notes = "",
    response = classOf[models.apidocs.Supplier],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Supplier id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def read(id: Long) = AuthorizedAction.async {
    Supplier.find(id) map (supplier => Ok(Json.toJson(supplier)))
  }

  @ApiOperation(
    nickname = "updateSupplier",
    value = "Edit supplier",
    notes = "",
    response = classOf[Void],
    httpMethod = "PATCH"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Supplier id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.SupplierUpdateBody"
      )
    )
  )
  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val updateReads: Reads[SupplierUpdateBody] = (
      (__ \ 'companyName).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'personalName).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'phoneNumber).read[String](minLength[String](1) keepAnd maxLength[String](120))
    )(SupplierUpdateBody.apply _)

    req.body.validate[SupplierUpdateBody].fold(
      valid = { j =>
        Supplier.save(id)(j.companyName, j.personalName, j.phoneNumber) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "deleteSupplier",
    value = "Remove supplier",
    notes = "",
    response = classOf[Void],
    httpMethod = "DELETE",
    consumes = "text/plain"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Supplier id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def delete(id: Long) = AuthorizedAction.async {
    Supplier.destroy(id) map (_ => NoContent)
  }

}
