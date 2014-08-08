package contexts

import utils.exceptions.{KiwiERPException, ResourceNotFound}

trait KiwiERPContext {

  def handleNotFound[U](re: KiwiERPException): PartialFunction[Throwable, U] = {
    case e: ResourceNotFound => throw re
  }

}
