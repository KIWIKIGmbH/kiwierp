package controllers

import contexts.{ClassifyPartsContext, CreatePartsContext, DeletePartsContext}
import jsons.PartsJson
import models.Parts
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.exceptions.InvalidRequest

object PartsController extends KiwiERPController {

  def index = AuthorizedAction.async { implicit req =>
    req.getQueryString("productId") filter isId map { productIdStr =>
      val productId = productIdStr.toLong

      Page(Parts.findAllByProductId(productId)) map { results =>
        val (partsList, page) = results

        Ok(PartsJson.index(partsList, page))
      }
    } getOrElse (throw new InvalidRequest)
  }


  def create = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class CreateForm(productId: Long, name: String, description: Option[String], neededQuantity: Int)

    val form = Form(
      mapping(
        "productId" -> longNumber(min = 1, max = MAX_LONG_NUMBER),
        "name" -> nonEmptyText(minLength = 1, maxLength = 120),
        "description" -> optional(text(maxLength = 500)),
        "neededQuantity" -> number(min = 1, max = MAX_NUMBER)
      )(CreateForm.apply)(CreateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      CreatePartsContext(f.productId, f.name, f.description, f.neededQuantity) map { parts =>
        CreatedWithLocation(PartsJson.create(parts))
      }
    }
  }

  def read(id: Long) = AuthorizedAction.async {
    Parts.find(id) map { parts =>
      Ok(PartsJson.read(parts))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class UpdateForm(name: String, description: Option[String], neededQuantity: Int)

    val form = Form(
      mapping(
        "name" -> nonEmptyText(minLength = 1, maxLength = 120),
        "description" -> optional(text(maxLength = 500)),
        "neededQuantity" -> number(min = 1, max = MAX_NUMBER)
      )(UpdateForm.apply)(UpdateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      Parts.save(id)(f.name, f.description, f.neededQuantity) map (_ => NoContent)
    }
  }

  def delete(id: Long) = AuthorizedAction.async {
    DeletePartsContext(id) map (_ => NoContent)
  }

  def classify(id: Long) = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class ClassifyForm(classifiedQuantity: Int, inventoryId: Option[Long], inventoryDescription: Option[String])

    val form = Form(
      mapping(
        "classifiedQuantity" -> number(min = 1, max = MAX_NUMBER),
        "inventoryId" -> optional(longNumber(min = 1, max = MAX_LONG_NUMBER)),
        "inventoryDescription" -> optional(text)
      )(ClassifyForm.apply)(ClassifyForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      f.inventoryId map { inventoryId =>
        if (f.inventoryDescription.isEmpty) {
          ClassifyPartsContext(id, f.classifiedQuantity, inventoryId) map (_ => NoContent)
        } else {
          throw new InvalidRequest
        }
      } getOrElse {
        ClassifyPartsContext(id, f.classifiedQuantity, f.inventoryDescription) map { inventory =>
          CreatedWithLocation(PartsJson.classify(inventory), Option("/inventories"))
        }
      }
    }
  }

}