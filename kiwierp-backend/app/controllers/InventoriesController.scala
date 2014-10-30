package controllers

import contexts.{CreateInventoryContext, DeleteInventoryContext}
import jsons.InventoryJson
import models.Inventory
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.exceptions.InvalidRequest

object InventoriesController extends KiwiERPController {

  def list = AuthorizedAction.async { implicit req =>
    req.getQueryString("partsId") filter isId map { partsIdStr =>
      val partsId = partsIdStr.toLong

      Page(Inventory.findAllByPartsId(partsId)) map { results =>
        val (inventories, page) = results

        Ok(InventoryJson.index(inventories, page))
      }
    } getOrElse (throw new InvalidRequest)
  }

  def create = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class CreateForm(partsId: Long, description: Option[String], quantity: Int)

    val form = Form(
      mapping(
        "partsId" -> longNumber(min = 0, max = MAX_LONG_NUMBER),
        "description" -> optional(text(maxLength = 500)),
        "quantity" -> number(min = 1, max = MAX_NUMBER)
      )(CreateForm.apply)(CreateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      CreateInventoryContext(f.partsId, f.description, f.quantity) map { inventory =>
        CreatedWithLocation(InventoryJson.create(inventory))
      }
    }
  }

  def read(id: Long) = AuthorizedAction.async {
    Inventory.find(id) map { inventory =>
      Ok(InventoryJson.read(inventory))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class UpdateForm(description: Option[String], quantity: Int)

    val form = Form(
      mapping(
        "description" -> optional(text(maxLength = 500)),
        "quantity" -> number(min = 1, max = MAX_NUMBER)
      )(UpdateForm.apply)(UpdateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      Inventory.save(id)(f.description, f.quantity) map (_ => NoContent)
    }
  }

  def delete(id: Long) = AuthorizedAction.async {
    DeleteInventoryContext(id) map (_ => NoContent)
  }

}
