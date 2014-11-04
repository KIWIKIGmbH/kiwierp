package controllers

import contexts.{CreateInventoryContext, DeleteInventoryContext}
import jsons.InventoryJson
import models.Inventory
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
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

  def create = AuthorizedAction.async(parse.json) { implicit req =>
    case class CreateForm(partsId: Long, description: Option[String], quantity: Int)

    implicit val createReads: Reads[CreateForm] = (
      (__ \ 'partsId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'description)
        .readNullable[String](maxLength[String](1) keepAnd minLength[String](500)) and
      (__ \ 'quantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(CreateForm.apply _)

    req.body.validate[CreateForm].fold(
      valid = { j =>
        CreateInventoryContext(j.partsId, j.description, j.quantity) map { inventory =>
          CreatedWithLocation(InventoryJson.create(inventory))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def read(id: Long) = AuthorizedAction.async {
    Inventory.find(id) map { inventory =>
      Ok(InventoryJson.read(inventory))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    case class UpdateForm(description: Option[String], quantity: Int)

    implicit val createReads: Reads[UpdateForm] = (
      (__ \ 'description)
        .readNullable[String](maxLength[String](1) keepAnd minLength[String](500)) and
      (__ \ 'quantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(UpdateForm.apply _)

    req.body.validate[UpdateForm].fold(
      valid = { j =>
        Inventory.save(id)(j.description, j.quantity) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def delete(id: Long) = AuthorizedAction.async {
    DeleteInventoryContext(id) map (_ => NoContent)
  }

}
