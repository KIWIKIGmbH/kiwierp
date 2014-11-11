package models.apidocs

import com.wordnik.swagger.annotations.ApiModelProperty
import org.joda.time.DateTime

import scala.annotation.meta.field

case class Product
(@(ApiModelProperty @field)(required = true) createdAt: DateTime,
 @(ApiModelProperty @field)(required = false) description: Option[String],
 @(ApiModelProperty @field)(required = true) id: Long,
 @(ApiModelProperty @field)(required = true) name: String,
 @(ApiModelProperty @field)(required = true) updatedAt: DateTime)
