package models.apidocs

import com.wordnik.swagger.annotations.ApiModelProperty

import scala.annotation.meta.field

case class AccessToken
(@(ApiModelProperty @field)(required = true) token_type: String,
 @(ApiModelProperty @field)(required = true) access_token: String,
 @(ApiModelProperty @field)(required = true) expires_in: Int)
