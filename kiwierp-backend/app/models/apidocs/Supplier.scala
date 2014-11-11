package models.apidocs

import com.wordnik.swagger.annotations.ApiModelProperty
import org.joda.time.DateTime

import scala.annotation.meta.field


case class Supplier
(@(ApiModelProperty @field)(required = true) companyName: String,
 @(ApiModelProperty @field)(required = true) createdAt: DateTime,
 @(ApiModelProperty @field)(required = true) id: Long,
 @(ApiModelProperty @field)(required = true) personalName: String,
 @(ApiModelProperty @field)(required = true) phoneNumber: String,
 @(ApiModelProperty @field)(required = true) updatedAt: DateTime)
