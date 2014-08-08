package controllers

import contexts.{UpdateInventoryFieldContext, CreateInventoryFieldContext, DeleteInventoryFieldContext}
import jsons.InventoryFieldJson
import models.InventoryField
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.exceptions.InvalidRequest

object InventoryFieldsController extends KiwiERPController {

  def index = AuthorizedAction.async { implicit req =>
    req.getQueryString("productId") filter isId map { productIdStr =>
      val productId = productIdStr.toLong

      Page(InventoryField.findAllByProductId(productId)) map { results =>
        val (inventoryFields, page) = results

        Ok(InventoryFieldJson.index(inventoryFields, page))
      }
    } getOrElse (throw new InvalidRequest)
  }

  def create = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class CreateForm(productId: Long, name: String, fieldType: String, isRequired: Boolean, min: Option[Int], max: Option[Int])

    val form = Form(
      mapping(
        "productId" -> longNumber(min = 1),
        "name" -> nonEmptyText,
        "fieldType" -> nonEmptyText,
        "isRequired" -> boolean,
        "min" -> optional(number),
        "max" -> optional(number)
      )(CreateForm.apply)(CreateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      CreateInventoryFieldContext(f.productId, f.name, f.fieldType, f.isRequired, f.min, f.max) map { inventoryField =>
        CreatedWithLocation(InventoryFieldJson.create(inventoryField))
      }
    }
  }

  def read(id: Long) = AuthorizedAction.async {
    InventoryField.find(id) map { inventoryField =>
      Ok(InventoryFieldJson.read(inventoryField))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.anyContent) { implicit request =>
    case class UpdateForm
    (name: String,
     fieldType: String,
     isRequired: Boolean,
     min: Option[Int],
     max: Option[Int])

    val form = Form(
      mapping(
        "name" -> nonEmptyText,
        "fieldType" -> nonEmptyText,
        "isRequired" -> boolean,
        "min" -> optional(number),
        "max" -> optional(number)
    )(UpdateForm.apply)(UpdateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      UpdateInventoryFieldContext(id, f.name, f.fieldType, f.isRequired, f.min, f.max) map (_ => NoContent)
    }
  }

  def delete(id: Long) = AuthorizedAction.async {
    DeleteInventoryFieldContext(id) map (_ => NoContent)
  }

}
