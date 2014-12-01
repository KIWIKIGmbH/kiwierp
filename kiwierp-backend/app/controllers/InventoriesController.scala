package controllers

import com.wordnik.swagger.annotations._
import contexts.{CreateInventoryContext, DeleteInventoryContext}
import jsons.InventoryJson
import models.Inventory
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

import scala.annotation.meta.field

case class InventoryCreationBody
(@(ApiModelProperty@field)(required = true) ComponentId: Long,
 @(ApiModelProperty@field)(required = false) description: Option[String],
 @(ApiModelProperty@field)(required = true) quantity: Int)

case class InventoryUpdateBody
(@(ApiModelProperty@field)(required = false) description: Option[String],
 @(ApiModelProperty@field)(required = true) quantity: Int)

@Api(
  value = "/inventory-management/inventories",
  description = "CRUD and list (search) API of inventory"
)
object InventoriesController extends KiwiERPController with InventoryJson {

  @ApiOperation(
    nickname = "listInventory",
    value = "find inventories by Component id",
    notes = "",
    response = classOf[models.apidocs.Inventories],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "ComponentId",
        value = "Component id",
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
    req.getQueryString("ComponentId") filter isId map { ComponentIdStr =>
      val ComponentId = ComponentIdStr.toLong

      Page(Inventory.findAllByComponentId(ComponentId)) map { results =>
        val (inventories, page) = results
        val json = Json.obj(
          "count" -> inventories.size,
          "page" -> page,
          "results" -> Json.toJson(inventories)
        )

        Ok(json)
      }
    } getOrElse (throw new InvalidRequest)
  }

  @ApiOperation(
    nickname = "createInventory",
    value = "Register inventory",
    notes = "",
    response = classOf[models.apidocs.Inventory],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.InventoryCreationBody"
      )
    )
  )
  def create = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val createReads: Reads[InventoryCreationBody] = (
      (__ \ 'componentId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'quantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(InventoryCreationBody.apply _)

    req.body.validate[InventoryCreationBody].fold(
      valid = { j =>
        CreateInventoryContext(j.ComponentId, j.description, j.quantity) map { inventory =>
          CreatedWithLocation(Json.toJson(inventory))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "readInventory",
    value = "Find inventory by ID",
    notes = "",
    response = classOf[models.apidocs.Inventory],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Inventory id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def read(id: Long) = AuthorizedAction.async {
    Inventory.find(id) map { inventory =>
      Ok(Json.toJson(inventory))
    }
  }

  @ApiOperation(
    nickname = "updateInventory",
    value = "Edit inventory",
    notes = "",
    response = classOf[Void],
    httpMethod = "PATCH"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Inventory id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.InventoryUpdateBody"
      )
    )
  )
  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val updateReads: Reads[InventoryUpdateBody] = (
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'quantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(InventoryUpdateBody.apply _)

    req.body.validate[InventoryUpdateBody].fold(
      valid = { j =>
        Inventory.save(id)(j.description, j.quantity) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "deleteInventory",
    value = "Remove inventory",
    notes = "",
    response = classOf[Void],
    httpMethod = "DELETE",
    consumes = "text/plain"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Inventory id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def delete(id: Long) = AuthorizedAction.async {
    DeleteInventoryContext(id) map (_ => NoContent)
  }

}
