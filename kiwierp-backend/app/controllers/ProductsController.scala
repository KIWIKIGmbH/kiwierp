package controllers

import contexts.DeleteProductContext
import jsons.ProductJson
import models.Product
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import utils.exceptions.InvalidRequest

object ProductsController extends KiwiERPController {

  def list = AuthorizedAction.async { implicit req =>
    Page(Product.findAll) map { results =>
      val (products, page) = results

      Ok(ProductJson.index(products, page))
    }
  }

  def create = AuthorizedAction.async(parse.json) { implicit req =>
    case class CreateForm(name: String, description: Option[String])

    implicit val createReads: Reads[CreateForm] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description).readNullable[String](minLength[String](1) keepAnd maxLength[String](120))
    )(CreateForm.apply _)

    req.body.validate[CreateForm].fold(
      valid = { j =>
        Product.create(j.name, j.description) map { product =>
          CreatedWithLocation(ProductJson.create(product))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def read(id: Long) = AuthorizedAction.async {
    Product.findWithPartsAndInventoriesAndInventoryOrders(id) map { product =>
      Ok(ProductJson.read(product))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    case class UpdateForm(name: String, description: Option[String])

    implicit val updateReads: Reads[UpdateForm] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description).readNullable[String](minLength[String](1) keepAnd maxLength[String](120))
    )(UpdateForm.apply _)

    req.body.validate[UpdateForm].fold(
      valid = { j =>
        Product.save(id)(j.name, j.description) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def delete(id: Long) = AuthorizedAction.async {
    DeleteProductContext(id) map (_ => NoContent)
  }

}
