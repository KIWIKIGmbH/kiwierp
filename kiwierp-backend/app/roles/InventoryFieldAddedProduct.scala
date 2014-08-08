package roles

import models.{InventoryField, Product}
import scalikejdbc.async.AsyncDBSession
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

trait InventoryFieldAddedProduct {

  this: Product =>

  def addInventoryField(name: String, fieldType: String, isRequired: Boolean, min: Option[Int], max: Option[Int])(implicit s: AsyncDBSession): Future[InventoryField] = {
    val MINIMUM_STRING_LENGTH = 0
    val isRangeValid = min.isDefined && max.isDefined && max.get >= min.get

    val isValidParameters = fieldType match {
      case "string" => isRangeValid && min.get > MINIMUM_STRING_LENGTH
      case "integer" | "decimal" => isRangeValid
      case "boolean" | "timestamp" => min.isEmpty && max.isEmpty
      case _ => false
    }

    if (isValidParameters) InventoryField.create(id, name, fieldType, isRequired, min, max)
    else throw new InvalidRequest
  }

}
