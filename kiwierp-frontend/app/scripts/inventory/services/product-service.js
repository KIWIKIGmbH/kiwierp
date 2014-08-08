'use strict';

angular.module('inventory.services')
  .factory('productService', ['$q', '$http', '$modal', '$routeParams', 'removeAlert',
    function ($q, $http, $modal, $routeParams, removeAlert) {
      var factory = {
        openProductForm: function (scope) {
          return $modal.open({
            templateUrl: '/views/inventory/modals/product-form.html',
            scope: scope
          });
        },

        openNewProductForm: function (scope) {
          return factory.openProductForm(scope);
        },

        removeProductAlert: function (scope) {
          scope.removeAlertMessage = "Are you sure to remove the product?";

          scope.removeAlertFunc = function () {
            scope.removeProduct();
          };
        },

        openEditProductForm: function(scope) {
          scope.productForm = {
            name: scope.product.name,
            description: scope.product.description
          };

          scope.productFormHeader = 'Edit Product';

          scope.isNewProductForm = false;

          return factory.openProductForm(scope);
        },

        product: function (token) {
          var productId = $routeParams.productId;

          if (!!productId) {
            var url = '/api/v1/products/' + productId + '?token=' + token;
            return $http.get(url);
          } else {
            return $q.reject();
          }
        },

        products: function (token) {
          var url = '/api/v1/products?token=' + token;
          return $http.get(url);
        },

        addProduct: function (name, description, token) {
          var url = '/api/v1/products?token=' + token;
          var data = {
            name: name,
            description: description
          };

          return $http.post(url, data);
        },

        editProduct: function (productId, name, description, token) {
          var url ='/api/v1/products/' + productId + '?token=' + token;
          var data = {
            name: name,
            description: description
          };

          return $http({
            method: 'patch',
            url: url,
            data: data
          });
        },

        removeProduct: function (productId, token) {
          var url ='/api/v1/products/' + productId + '?token=' + token;

          return $http.delete(url);
        }
      };

      return factory;
    }]);
