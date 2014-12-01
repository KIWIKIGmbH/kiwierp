package contexts

import models.Component
import org.joda.time.DateTime
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import roles.DeletedComponent
import scalikejdbc.async.{AsyncDB, AsyncDBSession}

import scala.concurrent.Future

class DeleteComponentContext private
(component: Component,
 deletedAt: DateTime = DateTime.now)(implicit tx: AsyncDBSession) {

  private def delete(): Future[Int] = {
    val deletedComponent = new Component(component) with DeletedComponent

    deletedComponent.deleteInventories(deletedAt) flatMap { _ =>
      deletedComponent.deleted(deletedAt)
    }
  }

}

object DeleteComponentContext {

  def apply(id: Long): Future[Int] = AsyncDB localTx { implicit tx =>
    Component.find(id) flatMap { component =>
      val cxt = new DeleteComponentContext(component)

      cxt.delete()
    }
  }

}
