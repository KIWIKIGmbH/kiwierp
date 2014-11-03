package controllers

import jsons.SupplierJson
import models.Supplier
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import utils.exceptions.InvalidRequest

object SuppliersController extends KiwiERPController {

  def list = AuthorizedAction.async { implicit req =>
    Page(Supplier.findAll) map { results =>
      val (suppliers, page) = results

      Ok(SupplierJson.index(suppliers, page))
    }
  }

  def create = AuthorizedAction.async(parse.json) { implicit req =>
    case class CreateForm(companyName: String, personalName: String, phoneNumber: String)

    implicit val createReads: Reads[CreateForm] = (
      (__ \ 'companyName).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'personalName).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'phoneNumber).read[String](minLength[String](1) keepAnd maxLength[String](120))
    )(CreateForm.apply _)

    req.body.validate[CreateForm].fold(
      valid = { j =>
        Supplier.create(j.companyName, j.personalName, j.phoneNumber) map { supplier =>
          CreatedWithLocation(SupplierJson.create(supplier))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def read(id: Long) = AuthorizedAction.async {
    Supplier.find(id) map { supplier =>
      Ok(SupplierJson.read(supplier))
    }
  }

  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    case class UpdateForm(companyName: String, personalName: String, phoneNumber: String)

    implicit val updateReads: Reads[UpdateForm] = (
      (__ \ 'companyName).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'personalName).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'phoneNumber).read[String](minLength[String](1) keepAnd maxLength[String](120))
    )(UpdateForm.apply _)

    req.body.validate[UpdateForm].fold(
      valid = { j =>
        Supplier.save(id)(j.companyName, j.personalName, j.phoneNumber) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  def delete(id: Long) = AuthorizedAction.async {
    Supplier.destroy(id) map (_ => NoContent)
  }

}
