package models.apidocs

import com.wordnik.swagger.annotations.ApiModelProperty
import org.joda.time.DateTime

import scala.annotation.meta.field

case class AccessToken
(@(ApiModelProperty @field)(required = true) createdAt: DateTime,
 @(ApiModelProperty @field)(required = true) expiresIn: Int,
 @(ApiModelProperty @field)(required = true) token: String,
 @(ApiModelProperty @field)(required = true) tokenType: String,
 @(ApiModelProperty @field)(required = true) userId: Long)
