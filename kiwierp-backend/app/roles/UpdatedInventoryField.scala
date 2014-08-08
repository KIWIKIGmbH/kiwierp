package roles

import models.{InventoryFieldValue, InventoryField}
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait UpdatedInventoryField {

  this: InventoryField =>

  // TODO
  def checkExistValues(existInventoryFieldValue: InventoryFieldValue with ExistInventoryFieldValue)
                      (name: String, fieldType: String, isRequired: Boolean, min: Option[Int], max: Option[Int]): Unit = ()

  def updated(name: String, fieldType: String, isRequired: Boolean, min: Option[Int], max: Option[Int])(implicit s: AsyncDBSession): Future[Int] =
    InventoryField.save(id)(name, fieldType, isRequired, min, max)

}
