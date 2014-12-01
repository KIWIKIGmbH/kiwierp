package controllers

import com.wordnik.swagger.annotations._
import contexts.{CreateOrderContext, UpdateOrderContext}
import jsons.OrderJson
import models.Order
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

import scala.annotation.meta.field

case class OrderCreationBody
(@(ApiModelProperty @field)(required = true) componentId: Long,
 @(ApiModelProperty @field)(required = true) supplierId: Long,
 @(ApiModelProperty @field)(required = true) quantity: Int,
 @(ApiModelProperty @field)(required = true) orderedDate: DateTime)

case class OrderUpdateBody
(@(ApiModelProperty @field)(required = true) status: String,
 @(ApiModelProperty @field)(required = true) statusChangedDate: DateTime)

@Api(
  value = "/inventory-management/orders",
  description = "CRUD and list (search) API of order data of component"
)
object OrdersController extends KiwiERPController with OrderJson {

  @ApiOperation(
    nickname = "listOrder",
    value = "find orders by component id",
    notes = "",
    response = classOf[models.apidocs.Orders],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "componentId",
        value = "component id",
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
    req.getQueryString("componentId") filter isId map { componentIdStr =>
      Page(Order.findAllByComponentId(componentIdStr.toLong))
    } getOrElse {
      Page(Order.findAll)
    } map { results =>
      val (orders, page) = results
      val json = Json.obj(
        "count" -> orders.size,
        "page" -> page,
        "results" -> Json.toJson(orders)
      )

      Ok(json)
    }
  }

  @ApiOperation(
    nickname = "createOrder",
    value = "Register order of component",
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
      (__ \ 'componentId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'supplierId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'quantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER)) and
      (__ \ 'orderedDate).read[DateTime](jodaDateReads(DATETIME_PATTERN))
    )(OrderCreationBody.apply _)

    req.body.validate[OrderCreationBody].fold(
      valid = { j =>
        CreateOrderContext(
          j.componentId,
          j.supplierId,
          j.quantity,
          j.orderedDate
        ) map { order =>
          CreatedWithLocation(Json.toJson(order))
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
    Order.find(id) map { order =>
      Ok(Json.toJson(order))
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
        UpdateOrderContext(id, j.status, j.statusChangedDate) map (_ => NoContent)
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
    Order.destroy(id) map (_ => NoContent)
  }

}
