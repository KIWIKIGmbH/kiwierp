package contexts

import models.{InventoryFieldValue, InventoryField}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{ExistInventoryFieldValue, UpdatedInventoryField}
import scalikejdbc.async.{AsyncDBSession, AsyncDB}

import scala.concurrent.Future

class UpdateInventoryFieldContext private
(inventoryField: InventoryField,
 inventoryFieldValues: Seq[InventoryFieldValue],
 name: String,
 fieldType: String,
 isRequired: Boolean,
 min: Option[Int],
 max: Option[Int])(implicit s: AsyncDBSession) {

  val updatedInventoryField = new InventoryField(inventoryField) with UpdatedInventoryField

  private def update(): Future[Int] = {
    inventoryFieldValues foreach { inventoryFieldValue =>
      val existInventoryFieldValue =
        new InventoryFieldValue(inventoryFieldValue) with ExistInventoryFieldValue

      updatedInventoryField.checkExistValues(existInventoryFieldValue)(name, fieldType, isRequired, min, max)
    }
    updatedInventoryField.updated(name, fieldType, isRequired, min, max)
  }

}

object UpdateInventoryFieldContext {

  def apply(id: Long, name: String, fieldType: String, isRequired: Boolean, min: Option[Int], max: Option[Int]): Future[Int] =
    AsyncDB withPool { implicit s =>
      InventoryField.findWithInventoryFieldValues(id) flatMap { inventoryField =>
        val inventoryFieldValues = inventoryField.inventoryFieldValues

        new UpdateInventoryFieldContext(inventoryField, inventoryFieldValues, name, fieldType, isRequired, min, max).update()
      }
    }

}
