package controllers

import com.wordnik.swagger.annotations._
import contexts.{CreateInventoryOrderContext, UpdateInventoryOrderContext}
import jsons.InventoryOrderJson
import models.InventoryOrder
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

import scala.annotation.meta.field

case class OrderCreationBody
(@(ApiModelProperty @field)(required = true) partsId: Long,
 @(ApiModelProperty @field)(required = true) supplierId: Long,
 @(ApiModelProperty @field)(required = true) quantity: Int,
 @(ApiModelProperty @field)(required = true) orderedDate: DateTime)

case class OrderUpdateBody
(@(ApiModelProperty @field)(required = true) status: String,
 @(ApiModelProperty @field)(required = true) statusChangedDate: DateTime)

@Api(
  value = "/inventoryorders",
  description = "CRUD and list (search) API of order data of parts"
)
object InventoryOrdersController extends KiwiERPController with InventoryOrderJson {

  @ApiOperation(
    nickname = "listOrder",
    value = "find orders by parts id",
    notes = "",
    response = classOf[models.apidocs.Orders],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "partsId",
        value = "Parts id",
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
    req.getQueryString("partsId") filter isId map { partsIdStr =>
      Page(InventoryOrder.findAllByPartsId(partsIdStr.toLong))
    } getOrElse {
      Page(InventoryOrder.findAll)
    } map { results =>
      val (inventoryOrders, page) = results
      val json = Json.obj(
        "count" -> inventoryOrders.size,
        "page" -> page,
        "results" -> Json.toJson(inventoryOrders)
      )

      Ok(json)
    }
  }

  @ApiOperation(
    nickname = "createOrder",
    value = "Register order of parts",
    notes = "",
    response = classOf[models.apidocs.Order],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.OrderCreationBody"
      )
    )
  )
  def create = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val createReads: Reads[OrderCreationBody] = (
      (__ \ 'partsId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'supplierId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'quantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER)) and
      (__ \ 'orderedDate).read[DateTime](jodaDateReads(DATETIME_PATTERN))
    )(OrderCreationBody.apply _)

    req.body.validate[OrderCreationBody].fold(
      valid = { j =>
        CreateInventoryOrderContext(
          j.partsId,
          j.supplierId,
          j.quantity,
          j.orderedDate
        ) map { inventoryOrder =>
          CreatedWithLocation(Json.toJson(inventoryOrder))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "readOrder",
    value = "Find order by ID",
    notes = "",
    response = classOf[models.apidocs.Order],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Order id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def read(id: Long) = AuthorizedAction.async {
    InventoryOrder.find(id) map { inventoryOrder =>
      Ok(Json.toJson(inventoryOrder))
    }
  }

  @ApiOperation(
    nickname = "updateOrder",
    value = "Edit Order",
    notes = "",
    response = classOf[Void],
    httpMethod = "PATCH"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Order id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.OrderUpdateBody"
      )
    )
  )
  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val updateReads: Reads[OrderUpdateBody] = (
      (__ \ 'status).read[String](minLength[String](1) keepAnd maxLength[String](10)) and
      (__ \ 'statusChangedDate).read[DateTime](jodaDateReads(DATETIME_PATTERN))
    )(OrderUpdateBody.apply _)

    req.body.validate[OrderUpdateBody].fold(
      valid = { j =>
        UpdateInventoryOrderContext(id, j.status, j.statusChangedDate) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "deleteOrder",
    value = "Remove order",
    notes = "",
    response = classOf[Void],
    httpMethod = "DELETE",
    consumes = "text/plain"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Order id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def delete(id: Long) = AuthorizedAction.async {
    InventoryOrder.destroy(id) map (_ => NoContent)
  }

}
