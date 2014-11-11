package models.apidocs

import com.wordnik.swagger.annotations.ApiModelProperty
import org.joda.time.DateTime

import scala.annotation.meta.field

case class Inventory
(@(ApiModelProperty@field)(required = true) createdAt: DateTime,
 @(ApiModelProperty@field)(required = false) description: Option[String],
 @(ApiModelProperty@field)(required = true) id: Long,
 @(ApiModelProperty@field)(required = true) partsId: Long,
 @(ApiModelProperty@field)(required = true) quantity: Int,
 @(ApiModelProperty@field)(required = true) updatedAt: DateTime)
