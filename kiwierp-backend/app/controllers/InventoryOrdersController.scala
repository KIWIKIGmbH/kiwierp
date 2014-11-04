package controllers

import contexts.{CreateInventoryOrderContext, UpdateInventoryOrderContext}
import jsons.InventoryOrderJson
import models.InventoryOrder
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

object InventoryOrdersController extends KiwiERPController with InventoryOrderJson {

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

  def create = AuthorizedAction.async(parse.json) { implicit req =>
    case class CreateForm(partsId: Long, supplierId: Long, quantity: Int, orderedDate: DateTime)

    implicit val createReads: Reads[CreateForm] = (
      (__ \ 'partsId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'supplierId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'quantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER)) and
      (__ \ 'orderedDate).read[DateTime](jodaDateReads(DATETIME_PATTERN))
    )(CreateForm.apply _)

    req.body.validate[CreateForm].fold(
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

  def read(id: Long) = AuthorizedAction.async {
    InventoryOrder.find(id) map { inventoryOrder =>
      Ok(Json.toJson(inventoryOrder))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    case class UpdateForm(status: String, statusChangedDate: DateTime)

    implicit val updateReads: Reads[UpdateForm] = (
      (__ \ 'status).read[String](minLength[String](1) keepAnd maxLength[String](10)) and
      (__ \ 'statusChangedDate).read[DateTime](jodaDateReads(DATETIME_PATTERN))
    )(UpdateForm.apply _)

    req.body.validate[UpdateForm].fold(
      valid = { j =>
        UpdateInventoryOrderContext(id, j.status, j.statusChangedDate) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def delete(id: Long) = AuthorizedAction.async {
    InventoryOrder.destroy(id) map (_ => NoContent)
  }

}
