package jsons

import models.Supplier
import play.api.libs.json.Json

object SupplierJson extends KiwiERPJson[Supplier] {

  def base(supplier: Supplier) = Json.obj(
    "companyName" -> supplier.companyName,
    "createdAt" -> supplier.createdAt,
    "id" -> supplier.id,
    "personalName" -> supplier.personalName,
    "phoneNumber" -> supplier.phoneNumber,
    "updatedAt" -> supplier.updatedAt
  )

}
