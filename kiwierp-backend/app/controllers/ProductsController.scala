package controllers

import com.github.mauricio.async.db.postgresql.exceptions.GenericDatabaseException
import com.wordnik.swagger.annotations._
import contexts._
import jsons.{ProductInventoryJson, ProductJson}
import models.{ProductInventory, Product}
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

case class ProductInventoryCreationBody
(@(ApiModelProperty @field)(required = true) description: Option[String],
 @(ApiModelProperty @field)(required = true) status: String,
 @(ApiModelProperty @field)(required = true) quantity: Int)

case class ProductInventoryUpdateBody
(@(ApiModelProperty @field)(required = true) description: Option[String],
 @(ApiModelProperty @field)(required = true) status: String,
 @(ApiModelProperty @field)(required = true) quantity: Int)

@Api(
  value = "/inventory-management/products",
  description = "CRUD and list (search) API of product"
)
object ProductsController extends KiwiERPController with ProductJson with ProductInventoryJson {

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
    Page(Product.findAllWithProductInventories) map { results =>
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
        } recover {
          case e: GenericDatabaseException => throw new InvalidRequest
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
    Product.findWithRelations(id) map { product =>
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
        Product.save(id)(j.name, j.description) map (_ => NoContent) recover {
          case e: GenericDatabaseException => throw new InvalidRequest
        }
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

  @ApiOperation(
    nickname = "listProductInventory",
    value = "find inventories of Product by Component id",
    notes = "",
    response = classOf[models.apidocs.ProductInventories],
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
      ),
      new ApiImplicitParam(
        name = "page",
        value = "Page Number",
        required = false,
        paramType = "query",
        dataType = "Int"
      ),
      new ApiImplicitParam(
        name = "status",
        value = "Status like ordered or delivered",
        required = false,
        paramType = "query",
        dataType = "String"
      )
    )
  )
  def listInventories(id: Long) = AuthorizedAction.async { implicit req =>
    Product.find(id) flatMap { _ =>
      req.getQueryString("status") map { status =>
        Page(ProductInventory.findAllByProductIdAndStatus(id, status))
      } getOrElse {
        Page(ProductInventory.findAllByProductId(id))
      } map { results =>
        val (productInventories, page) = results
        val json = Json.obj(
          "count" -> productInventories.size,
          "page" -> page,
          "results" -> Json.toJson(productInventories)
        )

        Ok(json)
      }
    }
  }

  @ApiOperation(
    nickname = "createProductInventory",
    value = "Register inventory of component",
    notes = "",
    response = classOf[models.apidocs.ProductInventory],
    httpMethod = "POST"
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
        dataType = "controllers.ProductInventoryCreationBody"
      )
    )
  )
  def createInventory(id: Long) = AuthorizedAction.async(parse.json) { implicit req =>
    implicit val createReads: Reads[ProductInventoryCreationBody] = (
      (__ \ 'description)
        .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
      (__ \ 'status).read[String](minLength[String](1) keepAnd maxLength[String](60)) and
      (__ \ 'quantity).read[Int](min[Int](0) keepAnd max[Int](MAX_NUMBER))
    )(ProductInventoryCreationBody.apply _)

    req.body.validate[ProductInventoryCreationBody].fold(
      valid = { j =>
        CreateProductInventoryContext(
          id,
          j.description,
          j.status,
          j.quantity
        ) map { inventory =>
          CreatedWithLocation(Json.toJson(inventory))
        }
      },
      invalid = ef => KiwiERPError.futureResult(new InvalidRequest())
    )
  }

  @ApiOperation(
    nickname = "readProductInventory",
    value = "Find an inventory of product by inventory ID",
    notes = "",
    response = classOf[models.apidocs.ProductInventory],
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
      ),
      new ApiImplicitParam(
        name = "inventoryId",
        value = "Product inventory id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def readInventory(id: Long, inventoryId: Long) = AuthorizedAction.async {
    ReadProductInventoryContext(id, inventoryId) map { productInventory =>
      Ok(Json.toJson(productInventory))
    }
  }

  @ApiOperation(
    nickname = "updateProductInventory",
    value = "Edit inventory of product",
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
        name = "inventoryId",
        value = "Product inventory id",
        required = true,
        paramType = "path",
        dataType = "Long"
      ),
      new ApiImplicitParam(
        name = "body",
        value = "Request body",
        required = true,
        paramType = "body",
        dataType = "controllers.ProductInventoryUpdateBody"
      )
    )
  )
  def updateInventory(id: Long, inventoryId: Long) =
    AuthorizedAction.async(parse.json) { implicit req =>
      implicit val updatedReads: Reads[ProductInventoryUpdateBody] = (
        (__ \ 'description)
          .readNullable[String](minLength[String](1) keepAnd maxLength[String](500)) and
        (__ \ 'status).read[String](minLength[String](1) keepAnd maxLength[String](10)) and
        (__ \ 'quantity).read[Int](min[Int](0) keepAnd max[Int](MAX_NUMBER))
      )(ProductInventoryUpdateBody.apply _)

      req.body.validate[ProductInventoryUpdateBody].fold(
        valid = { j =>
          UpdateProductInventoryContext(id, inventoryId, j.description, j.status, j.quantity) map {
            _ => NoContent
          }
        },
        invalid = ef => KiwiERPError.futureResult(new InvalidRequest)
      )
    }

  @ApiOperation(
    nickname = "deleteProductInventory",
    value = "Remove inventory of product",
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
      ),
      new ApiImplicitParam(
        name = "inventoryId",
        value = "Product inventory id",
        required = true,
        paramType = "path",
        dataType = "Long"
      )
    )
  )
  def deleteInventory(id: Long, inventoryId: Long) = AuthorizedAction.async {
    DeleteProductInventoryContext(id, inventoryId) map (_ => NoContent)
  }

}
