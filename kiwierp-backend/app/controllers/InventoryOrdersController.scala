package controllers

import contexts.{CreateInventoryOrderContext, UpdateInventoryOrderContext}
import jsons.InventoryOrderJson
import models.InventoryOrder
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object InventoryOrdersController extends KiwiERPController {

  def list = AuthorizedAction.async { implicit req =>
    req.getQueryString("partsId") filter isId map { partsIdStr =>
      Page(InventoryOrder.findAllByPartsId(partsIdStr.toLong))
    } getOrElse {
      Page(InventoryOrder.findAll)
    } map { results =>
      val (inventoryOrders, page) = results

      Ok(InventoryOrderJson.index(inventoryOrders, page))
    }
  }

  def create = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class CreateForm(partsId: Long, supplierId: Long, quantity: Int, orderedDate: DateTime)

    val form = Form(
      mapping(
        "partsId" -> longNumber(min = 1, max = MAX_LONG_NUMBER),
        "supplierId" -> longNumber(min = 1, max = MAX_LONG_NUMBER),
        "quantity" -> number(min = 1, max = MAX_NUMBER),
        "orderedDate" -> jodaDate(pattern = DATETIME_PATTERN)
      )(CreateForm.apply)(CreateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      CreateInventoryOrderContext(
        f.partsId,
        f.supplierId,
        f.quantity,
        f.orderedDate
      ) map { inventoryOrder =>
        CreatedWithLocation(InventoryOrderJson.create(inventoryOrder))
      }
    }
  }

  def read(id: Long) = AuthorizedAction.async {
    InventoryOrder.find(id) map { inventoryOrder =>
      Ok(InventoryOrderJson.read(inventoryOrder))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class UpdateForm(status: String, statusChangedDate: DateTime)

    val form = Form(
      mapping(
        "status" -> nonEmptyText(minLength = 1, maxLength = 10),
        "statusChangedDate" -> jodaDate(pattern = DATETIME_PATTERN)
      )(UpdateForm.apply)(UpdateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      UpdateInventoryOrderContext(id, f.status, f.statusChangedDate) map (_ => NoContent)
    }
  }

  def delete(id: Long) = AuthorizedAction.async {
    InventoryOrder.destroy(id) map (_ => NoContent)
  }

}
