package models.apidocs

import com.wordnik.swagger.annotations.ApiModelProperty
import org.joda.time.DateTime

import scala.annotation.meta.field

case class Order
(@(ApiModelProperty @field)(required = true) createdAt: DateTime,
 @(ApiModelProperty @field)(required = false) deliveredDate: Option[DateTime],
 @(ApiModelProperty @field)(required = true) id: Long,
 @(ApiModelProperty @field)(required = true) quantity: Int,
 @(ApiModelProperty @field)(required = true) orderedDate: DateTime,
 @(ApiModelProperty @field)(required = true) partsId: Long,
 @(ApiModelProperty @field)(required = false) shippedDate: Option[DateTime],
 @(ApiModelProperty @field)(required = true) status: String,
 @(ApiModelProperty @field)(required = true) updatedAt: DateTime)
