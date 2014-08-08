package controllers

import contexts.DeleteProductContext
import jsons.ProductJson
import models.Product
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object ProductsController extends KiwiERPController {

  def index = AuthorizedAction.async { implicit req =>
    Page(Product.findAll) map { results =>
      val (products, page) = results

      Ok(ProductJson.index(products, page))
    }
  }

  def create = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class CreateForm(name: String, description: Option[String])

    val form = Form(
      mapping(
        "name" -> nonEmptyText,
        "description" -> optional(text)
      )(CreateForm.apply)(CreateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      Product.create(f.name, f.description) map { product =>
        CreatedWithLocation(ProductJson.create(product))
      }
    }
  }

  def read(id: Long) = AuthorizedAction.async {
    Product.findWithPartsAndInventoriesAndInventoryOrders(id) map { product =>
      Ok(ProductJson.read(product))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class UpdateForm(name: String, description: Option[String])

    val form = Form(
      mapping(
        "name" -> text,
        "description" -> optional(text)
      )(UpdateForm.apply)(UpdateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      Product.save(id)(f.name, f.description) map (_ => NoContent)
    }
  }

  def delete(id: Long) = AuthorizedAction.async {
    DeleteProductContext(id) map (_ => NoContent)
  }

}
