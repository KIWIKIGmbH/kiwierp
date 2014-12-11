package models.apidocs

import com.wordnik.swagger.annotations.ApiModelProperty
import org.joda.time.DateTime

import scala.annotation.meta.field

case class ProductInventory
(@(ApiModelProperty@field)(required = true) createdAt: DateTime,
 @(ApiModelProperty@field)(required = false) description: Option[String],
 @(ApiModelProperty@field)(required = true) id: Long,
 @(ApiModelProperty@field)(required = true) productId: Long,
 @(ApiModelProperty@field)(required = true) quantity: Int,
 @(ApiModelProperty@field)(required = true) status: String,
 @(ApiModelProperty@field)(required = true) updatedAt: DateTime)
