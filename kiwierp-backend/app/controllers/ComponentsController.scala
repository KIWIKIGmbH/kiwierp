package controllers

import com.github.mauricio.async.db.postgresql.exceptions.GenericDatabaseException
import com.wordnik.swagger.annotations._
import contexts._
import jsons.{ComponentInventoryJson, ComponentJson}
import models.{Component, ComponentInventory}
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

case class ComponentInventoryCreationBody
(@(ApiModelProperty@field)(required = false) description: Option[String],
 @(ApiModelProperty@field)(required = true) quantity: Int)

case class ComponentInventoryUpdateBody
(@(ApiModelProperty@field)(required = false) description: Option[String],
 @(ApiModelProperty@field)(required = true) quantity: Int)

@Api(
  value = "/inventory-management/components",
  description = "CRUD and list (search) API of components"
)
object ComponentsController
  extends KiwiERPController
  with ComponentJson
  with ComponentInventoryJson {

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
    response = classOf[models.apidocs.ComponentInventory],
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

  @ApiOperation(
    nickname = "listComponentInventory",
    value = "find inventories of Component by Component id",
    notes = "",
    response = classOf[models.apidocs.ComponentInventories],
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
  def listInventories(id: Long) = AuthorizedAction.async { implicit req =>
    Component.find(id) flatMap { _ =>
      Page(ComponentInventory.findAllByComponentId(id)) map { results =>
        val (inventories, page) = results
        val json = Json.obj(
          "count" -> inventories.size,
          "page" -> page,
          "results" -> Json.toJson(inventories)
        )

        Ok(json)
      }
    }
  }

  @ApiOperation(
    nickname = "createComponentInventory",
    value = "Register inventory of component",
    notes = "",
    response = classOf[models.apidocs.ComponentInventory],
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
        dataType = "controllers.ComponentInventoryCreationBody"
      )
    )
  )
  def createInventory(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val createReads: Reads[ComponentInventoryCreationBody] = (
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'quantity).read[Int](min[Int](0) keepAnd max[Int](MAX_NUMBER))
    )(ComponentInventoryCreationBody.apply _)

    req.body.validate[ComponentInventoryCreationBody].fold(
      valid = { j =>
        CreateComponentInventoryContext(id, j.description, j.quantity) map { inventory =>
          CreatedWithLocation(Json.toJson(inventory))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "readComponentInventory",
    value = "Find an inventory of component by inventory ID",
    notes = "",
    response = classOf[models.apidocs.ComponentInventory],
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
      ),
      new ApiImplicitParam(
        name = "inventoryId",
        value = "Component inventory id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def readInventory(id: Long, inventoryId: Long) = AuthorizedAction.async {
    Component.find(id) flatMap { _ =>
      ReadComponentInventoryContext(id, inventoryId) map { inventory =>
        Ok(Json.toJson(inventory))
      }
    }
  }

  @ApiOperation(
    nickname = "updateComponentInventory",
    value = "Edit inventory of component",
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
        name = "inventoryId",
        value = "Component inventory id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.ComponentInventoryUpdateBody"
      )
    )
  )
  def updateInventory(id: Long, inventoryId: Long) =
    AuthorizedAction.async(parse.json) {implicit req =>
      implicit val updateReads: Reads[ComponentInventoryUpdateBody] = (
        (__ \ 'description)
          .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
        (__ \ 'quantity).read[Int](min[Int](0) keepAnd max[Int](MAX_NUMBER))
      )(ComponentInventoryUpdateBody.apply _)

      req.body.validate[ComponentInventoryUpdateBody].fold(
        valid = { j =>
          UpdateComponentInventoryContext(id, inventoryId, j.description, j.quantity) map { _ =>
            NoContent
          }
        },
        invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
      )
    }

  @ApiOperation(
    nickname = "deleteComponentInventory",
    value = "Remove inventory of component",
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
      ),
      new ApiImplicitParam(
        name = "inventoryId",
        value = "Component inventory id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def deleteInventory(id: Long, inventoryId: Long) = AuthorizedAction.async {
    DeleteComponentInventoryContext(id, inventoryId) map (_ => NoContent)
  }

}
