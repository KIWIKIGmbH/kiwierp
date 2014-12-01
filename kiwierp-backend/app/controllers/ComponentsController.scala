package controllers

import com.github.mauricio.async.db.postgresql.exceptions.GenericDatabaseException
import com.wordnik.swagger.annotations._
import contexts.{ClassifyComponentContext, CreateComponentContext, DeleteComponentContext}
import jsons.ComponentJson
import models.Component
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

import scala.annotation.meta.field

case class ComponentCreationBody
(@(ApiModelProperty @field)(required = true) productId: Long,
 @(ApiModelProperty @field)(required = true) name: String,
 @(ApiModelProperty @field)(required = false) description: Option[String],
 @(ApiModelProperty @field)(required = true) neededQuantity: Int)

case class ComponentUpdateBody
(@(ApiModelProperty @field)(required = true) name: String,
 @(ApiModelProperty @field)(required = false) description: Option[String],
 @(ApiModelProperty @field)(required = true) neededQuantity: Int)

case class ComponentClassificationBody
(@(ApiModelProperty @field)(required = true) classifiedQuantity: Int,
 @(ApiModelProperty @field)(required = false) inventoryId: Option[Long],
 @(ApiModelProperty @field)(required = false) inventoryDescription: Option[String])

@Api(
  value = "/inventory-management/components",
  description = "CRUD and list (search) API of components"
)
object ComponentsController extends KiwiERPController with ComponentJson {

  @ApiOperation(
    nickname = "listComponent",
    value = "find Component by product id",
    notes = "",
    response = classOf[models.apidocs.Components],
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

      Page(Component.findAllByProductId(productId)) map { results =>
        val (components, page) = results
        val json = Json.obj(
          "count" -> components.size,
          "page" -> page,
          "results" -> Json.toJson(components)
        )

        Ok(json)
      }
    } getOrElse (throw new InvalidRequest)
  }

  @ApiOperation(
    nickname = "createComponent",
    value = "Register Component",
    notes = "",
    response = classOf[models.apidocs.Component],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.ComponentCreationBody"
      )
    )
  )
  def create = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val createReads: Reads[ComponentCreationBody] = (
      (__ \ 'productId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'neededQuantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(ComponentCreationBody.apply _)

    req.body.validate[ComponentCreationBody].fold(
      valid = { j =>
        CreateComponentContext(j.productId, j.name, j.description, j.neededQuantity) map { Component =>
          CreatedWithLocation(Json.toJson(Component))
        } recover {
          case e: GenericDatabaseException => throw new InvalidRequest
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "readComponent",
    value = "Find Component by ID",
    notes = "",
    response = classOf[models.apidocs.Component],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Component id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def read(id: Long) = AuthorizedAction.async {
    Component.find(id) map { Component =>
      Ok(Json.toJson(Component))
    }
  }

  @ApiOperation(
    nickname = "updateComponent",
    value = "Edit Component",
    notes = "",
    response = classOf[Void],
    httpMethod = "PATCH"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Component id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.ComponentUpdateBody"
      )
    )
  )
  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val updateReads: Reads[ComponentUpdateBody] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'neededQuantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(ComponentUpdateBody.apply _)

    req.body.validate[ComponentUpdateBody].fold(
      valid = { j =>
        Component.save(id)(j.name, j.description, j.neededQuantity) map (_ => NoContent) recover {
          case e: GenericDatabaseException => throw new InvalidRequest
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "deleteComponent",
    value = "Remove Component",
    notes = "",
    response = classOf[Void],
    httpMethod = "DELETE",
    consumes = "text/plain"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Component id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def delete(id: Long) = AuthorizedAction.async {
    DeleteComponentContext(id) map (_ => NoContent)
  }

  @ApiOperation(
    nickname = "classifyComponent",
    value = "Classify Component into inventory",
    notes = "Require either inventoryId or inventoryDescription on the request parameters.",
    response = classOf[models.apidocs.Inventory],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Component id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.ComponentClassificationBody"
      )
    )
  )
  def classify(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val classifyReads: Reads[ComponentClassificationBody] = (
      (__ \ 'classifiedQuantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER)) and
      (__ \ 'inventoryId).readNullable[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'inventoryDescription)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500))
    )(ComponentClassificationBody.apply _)

    req.body.validate[ComponentClassificationBody].fold(
      valid = { j =>
        j.inventoryId map { inventoryId =>
          if (j.inventoryDescription.isEmpty) {
            ClassifyComponentContext(id, j.classifiedQuantity, inventoryId) map (_ => NoContent)
          } else {
            throw new InvalidRequest
          }
        } getOrElse {
          ClassifyComponentContext(id, j.classifiedQuantity, j.inventoryDescription) map { inventory =>
            CreatedWithLocation(Json.toJson(inventory), Option("/inventories"))
          }
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

}
