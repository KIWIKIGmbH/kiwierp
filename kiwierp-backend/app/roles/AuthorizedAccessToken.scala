package roles

import models.AccessToken
import utils.exceptions.ExpiredToken

trait AuthorizedAccessToken {

  this: AccessToken =>

  def checkExpiration() = if (isExpired) throw new ExpiredToken

}
