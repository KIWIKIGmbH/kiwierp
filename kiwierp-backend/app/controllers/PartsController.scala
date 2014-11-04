package controllers

import contexts.{ClassifyPartsContext, CreatePartsContext, DeletePartsContext}
import jsons.PartsJson
import models.Parts
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

object PartsController extends KiwiERPController with PartsJson {

  def list = AuthorizedAction.async { implicit req =>
    req.getQueryString("productId") filter isId map { productIdStr =>
      val productId = productIdStr.toLong

      Page(Parts.findAllByProductId(productId)) map { results =>
        val (partsList, page) = results
        val json = Json.obj(
          "count" -> partsList.size,
          "page" -> page,
          "results" -> Json.toJson(partsList)
        )

        Ok(json)
      }
    } getOrElse (throw new InvalidRequest)
  }


  def create = AuthorizedAction.async(parse.json) { implicit req =>
    case class CreateForm
    (productId: Long,
     name: String,
     description: Option[String],
     neededQuantity: Int)

    implicit val createReads: Reads[CreateForm] = (
      (__ \ 'productId).read[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'neededQuantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(CreateForm.apply _)

    req.body.validate[CreateForm].fold(
      valid = { j =>
        CreatePartsContext(j.productId, j.name, j.description, j.neededQuantity) map { parts =>
          CreatedWithLocation(Json.toJson(parts))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def read(id: Long) = AuthorizedAction.async {
    Parts.find(id) map { parts =>
      Ok(Json.toJson(parts))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    case class UpdateForm(name: String, description: Option[String], neededQuantity: Int)

    implicit val updateReads: Reads[UpdateForm] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'neededQuantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER))
    )(UpdateForm.apply _)

    req.body.validate[UpdateForm].fold(
      valid = { j =>
        Parts.save(id)(j.name, j.description, j.neededQuantity) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def delete(id: Long) = AuthorizedAction.async {
    DeletePartsContext(id) map (_ => NoContent)
  }

  def classify(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    case class ClassifyForm
    (classifiedQuantity: Int,
     inventoryId: Option[Long],
     inventoryDescription: Option[String])

    implicit val classifyReads: Reads[ClassifyForm] = (
      (__ \ 'classifiedQuantity).read[Int](min[Int](1) keepAnd max[Int](MAX_NUMBER)) and
      (__ \ 'inventoryId).readNullable[Long](min[Long](1) keepAnd max[Long](MAX_LONG_NUMBER)) and
      (__ \ 'inventoryDescription)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500))
    )(ClassifyForm.apply _)

    req.body.validate[ClassifyForm].fold(
      valid = { j =>
        j.inventoryId map { inventoryId =>
          if (j.inventoryDescription.isEmpty) {
            ClassifyPartsContext(id, j.classifiedQuantity, inventoryId) map (_ => NoContent)
          } else {
            throw new InvalidRequest
          }
        } getOrElse {
          ClassifyPartsContext(id, j.classifiedQuantity, j.inventoryDescription) map { inventory =>
            CreatedWithLocation(Json.toJson(inventory), Option("/inventories"))
          }
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

}
