package controllers

import com.wordnik.swagger.annotations._
import contexts.{ClassifyPartsContext, CreatePartsContext, DeletePartsContext}
import jsons.PartsJson
import models.Parts
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

import scala.annotation.meta.field

case class PartsCreationBody
(@(ApiModelProperty @field)(required = true) productId: Long,
 @(ApiModelProperty @field)(required = true) name: String,
 @(ApiModelProperty @field)(required = false) description: Option[String],
 @(ApiModelProperty @field)(required = true) neededQuantity: Int)

case class PartsUpdateBody
(@(ApiModelProperty @field)(required = true) name: String,
 @(ApiModelProperty @field)(required = false) description: Option[String],
 @(ApiModelProperty @field)(required = true) neededQuantity: Int)

case class PartsClassificationBody
(@(ApiModelProperty @field)(required = true) classifiedQuantity: Int,
 @(ApiModelProperty @field)(required = false) inventoryId: Option[Long],
 @(ApiModelProperty @field)(required = false) inventoryDescription: Option[String])

@Api(
  value = "/parts",
  description = "CRUD and list (search) API of parts"
)
object PartsController extends KiwiERPController with PartsJson {

  @ApiOperation(
    nickname = "listParts",
    value = "find parts by product id",
    notes = "",
    response = classOf[models.apidocs.PartsSeq],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "productId",
        value = "Product id",
        required = true,
        paramType = "query",
        dataType = "Long"
      ),
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
    req.getQueryString("productId") filter isId map { productIdStr =>
      val productId = productIdStr.toLong

      Page(Parts.findAllByProductId(productId)) map { results =>
        val (partsList, page) = results
        val json = Json.obj(
          "count" -> partsList.size,
          "page" -> page,
          "results" -> Json.toJson(partsList)
        )

        Ok(json)
      }
    } getOrElse (throw new InvalidRequest)
  }

  @ApiOperation(
    nickname = "createParts",
    value = "Register parts",
    notes = "",
    response = classOf[models.apidocs.Parts],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.PartsCreationBody"
      )
    )
  )
  def create = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val createReads: Reads[PartsCreationBody] = (
      (__ \ 'productId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'neededQuantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(PartsCreationBody.apply _)

    req.body.validate[PartsCreationBody].fold(
      valid = { j =>
        CreatePartsContext(j.productId, j.name, j.description, j.neededQuantity) map { parts =>
          CreatedWithLocation(Json.toJson(parts))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "readParts",
    value = "Find parts by ID",
    notes = "",
    response = classOf[models.apidocs.Parts],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Parts id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def read(id: Long) = AuthorizedAction.async {
    Parts.find(id) map { parts =>
      Ok(Json.toJson(parts))
    }
  }

  @ApiOperation(
    nickname = "updateParts",
    value = "Edit parts",
    notes = "",
    response = classOf[Void],
    httpMethod = "PATCH"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Parts id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.PartsUpdateBody"
      )
    )
  )
  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val updateReads: Reads[PartsUpdateBody] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'neededQuantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(PartsUpdateBody.apply _)

    req.body.validate[PartsUpdateBody].fold(
      valid = { j =>
        Parts.save(id)(j.name, j.description, j.neededQuantity) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "deleteParts",
    value = "Remove parts",
    notes = "",
    response = classOf[Void],
    httpMethod = "DELETE",
    consumes = "text/plain"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Parts id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def delete(id: Long) = AuthorizedAction.async {
    DeletePartsContext(id) map (_ => NoContent)
  }

  @ApiOperation(
    nickname = "classifyParts",
    value = "Classify parts into inventory",
    notes = "Require either inventoryId or inventoryDescription on the request parameters.",
    response = classOf[models.apidocs.Inventory],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Parts id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.PartsClassificationBody"
      )
    )
  )
  def classify(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val classifyReads: Reads[PartsClassificationBody] = (
      (__ \ 'classifiedQuantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER)) and
      (__ \ 'inventoryId).readNullable[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'inventoryDescription)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500))
    )(PartsClassificationBody.apply _)

    req.body.validate[PartsClassificationBody].fold(
      valid = { j =>
        j.inventoryId map { inventoryId =>
          if (j.inventoryDescription.isEmpty) {
            ClassifyPartsContext(id, j.classifiedQuantity, inventoryId) map (_ => NoContent)
          } else {
            throw new InvalidRequest
          }
        } getOrElse {
          ClassifyPartsContext(id, j.classifiedQuantity, j.inventoryDescription) map { inventory =>
            CreatedWithLocation(Json.toJson(inventory), Option("/inventories"))
          }
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

}
