package models.apidocs

import com.wordnik.swagger.annotations.ApiModelProperty

import scala.annotation.meta.field

case class PartsSeq
(@(ApiModelProperty @field)(required = true) count: Int,
 @(ApiModelProperty @field)(required = true) page: Int,
 @(ApiModelProperty @field)(required = true) results: Seq[Parts])