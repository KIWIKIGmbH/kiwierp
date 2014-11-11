package controllers

import com.wordnik.swagger.annotations._
import contexts.DeleteProductContext
import jsons.ProductJson
import models.Product
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.exceptions.InvalidRequest

import scala.annotation.meta.field

case class ProductCreationBody
(@(ApiModelProperty @field)(required = true) name: String,
 @(ApiModelProperty @field)(required = false) description: Option[String])

case class ProductUpdateBody
(@(ApiModelProperty @field)(required = true) name: String,
 @(ApiModelProperty @field)(required = false) description: Option[String])

@Api(
  value = "/products",
  description = "CRUD and list (search) API of product"
)
object ProductsController extends KiwiERPController with ProductJson {

  @ApiOperation(
    nickname = "listProduct",
    value = "find products",
    notes = "",
    response = classOf[models.apidocs.Products],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "page",
        value = "Page Number",
        required = false,
        paramType = "query",
        dataType = "Int"
      )
    )
  )
  def list = AuthorizedAction.async { implicit req =>
    Page(Product.findAll) map { results =>
      val (products, page) = results
      val json = Json.obj(
        "count" -> products.size,
        "page" -> page,
        "results" -> Json.toJson(products)
      )

      Ok(json)
    }
  }

  @ApiOperation(
    nickname = "createProduct",
    value = "Register product",
    notes = "",
    response = classOf[models.apidocs.Product],
    httpMethod = "POST"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.ProductCreationBody"
      )
    )
  )
  def create = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val createReads: Reads[ProductCreationBody] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description).readNullable[String](minLength[String](1) keepAnd maxLength[String](120))
    )(ProductCreationBody.apply _)

    req.body.validate[ProductCreationBody].fold(
      valid = { j =>
        Product.create(j.name, j.description) map { product =>
          CreatedWithLocation(Json.toJson(product))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "readProduct",
    value = "Find product by ID",
    notes = "",
    response = classOf[models.apidocs.ProductWithRelations],
    httpMethod = "GET"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Product id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def read(id: Long) = AuthorizedAction.async {
    Product.findWithPartsAndInventoriesAndInventoryOrders(id) map { product =>
      Ok(Json.toJson(product))
    }
  }

  @ApiOperation(
    nickname = "updateProduct",
    value = "Edit product",
    notes = "",
    response = classOf[Void],
    httpMethod = "PATCH"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Product id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.ProductUpdateBody"
      )
    )
  )
  def update(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val updateReads: Reads[ProductUpdateBody] = (
      (__ \ 'name).read[String](minLength[String](1) keepAnd maxLength[String](120)) and
      (__ \ 'description).readNullable[String](minLength[String](1) keepAnd maxLength[String](120))
    )(ProductUpdateBody.apply _)

    req.body.validate[ProductUpdateBody].fold(
      valid = { j =>
        Product.save(id)(j.name, j.description) map (_ => NoContent)
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
    )
  }

  @ApiOperation(
    nickname = "deleteProduct",
    value = "Remove product",
    notes = "",
    response = classOf[Void],
    httpMethod = "DELETE",
    consumes = "text/plain"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "id",
        value = "Product id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def delete(id: Long) = AuthorizedAction.async {
    DeleteProductContext(id) map (_ => NoContent)
  }

}
