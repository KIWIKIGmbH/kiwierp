package contexts

import models.{Order, Component}
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.{DeliveredOrder, DeliveredComponent, ShippedOrder}
import scalikejdbc.async.{AsyncDB, AsyncDBSession}
import utils.exceptions.InvalidRequest

import scala.concurrent.Future

class UpdateOrderContext private (order: Order)(implicit s: AsyncDBSession) {

  private def ship(shippedDate: DateTime): Future[Int] = {
    val shippedOrder = new Order(order) with ShippedOrder

    shippedOrder.checkStatus()
    shippedOrder.shipped(shippedDate)
  }

  private def deliver(component: Component, deliveredDate: DateTime): Future[Int] = {
    val deliveredOrder = new Order(order) with DeliveredOrder
    val deliveredComponent = new Component(component) with DeliveredComponent

    deliveredOrder.checkStatus()
    deliveredOrder.delivered(deliveredDate) flatMap { _ =>
      deliveredComponent.archive(deliveredOrder)
    }
  }

}

object UpdateOrderContext extends KiwiERPContext {

  def apply(id: Long, status: String, statusChangedDate: DateTime): Future[Int] = status match {
    case "shipped" => AsyncDB withPool { implicit s =>
      Order.find(id) flatMap { order =>
        val cxt = new UpdateOrderContext(order)

        cxt.ship(statusChangedDate)
      }
    }
    case "delivered" => AsyncDB localTx { implicit tx =>
      Order.findWithComponent(id) flatMap { order =>
        val component = order.component.get
        val cxt = new UpdateOrderContext(order)

        cxt.deliver(component, statusChangedDate)
      }
    }
    case _ => throw new InvalidRequest
  }

}
