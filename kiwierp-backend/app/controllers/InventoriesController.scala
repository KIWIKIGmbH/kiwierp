package controllers

import contexts.{CreateInventoryContext, DeleteInventoryContext}
import jsons.InventoryJson
import models.Inventory
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

object InventoriesController extends KiwiERPController with InventoryJson {

  def list = AuthorizedAction.async { implicit req =>
    req.getQueryString("partsId") filter isId map { partsIdStr =>
      val partsId = partsIdStr.toLong

      Page(Inventory.findAllByPartsId(partsId)) map { results =>
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
          CreatedWithLocation(Json.toJson(inventory))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def read(id: Long) = AuthorizedAction.async {
    Inventory.find(id) map { inventory =>
      Ok(Json.toJson(inventory))
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
