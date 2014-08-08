package roles

import models.{Parts, Product}
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait PartsAddedProduct {

  this: Product =>

  def addParts(name: String, description: Option[String], neededQuantity: Int)(implicit s: AsyncDBSession): Future[Parts] =
    Parts.create(id, name, description, neededQuantity)

}
