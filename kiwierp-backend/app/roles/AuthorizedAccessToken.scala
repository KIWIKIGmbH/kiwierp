package roles

import models.AccessToken
import utils.exceptions.ExpiredToken

trait AuthorizedAccessToken {

  this: AccessToken =>

  def checkExpiration(): Unit = if (isExpired) throw new ExpiredToken

}
