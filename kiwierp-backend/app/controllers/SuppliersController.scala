package controllers

import jsons.SupplierJson
import models.Supplier
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object SuppliersController extends KiwiERPController {

  def index = AuthorizedAction.async { implicit req =>
    Page(Supplier.findAll) map { results =>
      val (suppliers, page) = results

      Ok(SupplierJson.index(suppliers, page))
    }
  }

  def create = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class CreateForm(companyName: String, personalName: String, phoneNumber: String)

    val form = Form(
      mapping(
        "companyName" -> nonEmptyText,
        "personalName" -> nonEmptyText,
        "phoneNumber" -> nonEmptyText
      )(CreateForm.apply)(CreateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      Supplier.create(f.companyName, f.personalName, f.phoneNumber) map { supplier =>
        CreatedWithLocation(SupplierJson.create(supplier))
      }
    }
  }

  def read(id: Long) = AuthorizedAction.async {
    Supplier.find(id) map { supplier =>
      Ok(SupplierJson.read(supplier))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.urlFormEncoded) { implicit req =>
    case class UpdateForm(companyName: String, personalName: String, phoneNumber: String)

    val form = Form(
      mapping(
        "companyName" -> nonEmptyText,
        "personalName" -> nonEmptyText,
        "phoneNumber" -> nonEmptyText
      )(UpdateForm.apply)(UpdateForm.unapply))

    form.bindFromRequestAndCheckErrors { f =>
      Supplier.save(id)(f.companyName, f.personalName, f.phoneNumber) map (_ => NoContent)
    }
  }

  def delete(id: Long) = AuthorizedAction.async {
    Supplier.destroy(id) map (_ => NoContent)
  }

}
