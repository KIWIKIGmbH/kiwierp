package roles

import models.{Component, Product}
import scalikejdbc.async.AsyncDBSession

import scala.concurrent.Future

trait ComponentAddedProduct {

  this: Product =>

  def addComponent(name: String,
                   description: Option[String],
                   neededQuantity: Int)(implicit s: AsyncDBSession): Future[Component] =
    Component.create(id, name, description, neededQuantity)

}
