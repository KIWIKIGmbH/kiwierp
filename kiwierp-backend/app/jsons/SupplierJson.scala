package jsons

import models.Supplier
import play.api.libs.json.{JsValue, Json, Writes}

trait SupplierJson extends KiwiERPJson {

  implicit val supplierWrites = new Writes[Supplier] {
    def writes(supplier: Supplier): JsValue = Json.obj(
      "companyName" -> supplier.companyName,
      "createdAt" -> dateTimeToString(supplier.createdAt),
      "id" -> supplier.id,
      "personalName" -> supplier.personalName,
      "phoneNumber" -> supplier.phoneNumber,
      "updatedAt" -> dateTimeToString(supplier.updatedAt)
    )
  }

}
