package contexts

import models.{Component, Order, Supplier}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{OrderReceivedSupplier, OrderedComponent}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class CreateOrderContext private
(component: Component,
 supplier: Supplier,
 quantity: Int,
 orderedDate: DateTime)(implicit s: AsyncDBSession) {

  private def create(): Future[Order] = {
    val orderedComponent = new Component(component) with OrderedComponent
    val orderReceivedSupplier = new Supplier(supplier) with OrderReceivedSupplier

    orderReceivedSupplier.receiveOrder(orderedComponent, quantity, orderedDate)
  }

}

object CreateOrderContext extends KiwiERPContext {

  def apply(componentId: Long,
            supplierId: Long,
            quantity: Int,
            orderedDate: DateTime): Future[Order] = AsyncDB withPool { implicit s =>
    Component.find(componentId) flatMap { component =>
      Supplier.find(supplierId) flatMap { supplier =>
        val cxt = new CreateOrderContext(component, supplier, quantity, orderedDate)

        cxt.create()
      }
    } recover handleNotFound(new InvalidRequest)
  }

}
