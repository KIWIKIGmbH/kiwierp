package controllers

import jsons.InventoryFieldValueJson
import models.InventoryFieldValue
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object InventoryFieldValuesController extends KiwiERPController {

  def index = TODO

  def create = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class CreateForm(inventoryId: Long, inventoryFieldId: Long, value: String)

    val form = Form(
      mapping(
        "inventoryId" -> longNumber(min = 1),
        "inventoryFieldId" -> longNumber(min = 1),
        "value" -> nonEmptyText
      )(CreateForm.apply)(CreateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      InventoryFieldValue.create(f.inventoryId, f.inventoryFieldId, f.value) map { inventoryFieldValue =>
        CreatedWithLocation(InventoryFieldValueJson.create(inventoryFieldValue))
      }
    }
  }

  def read = TODO

  def update = TODO

  def delete = TODO

}
