'use strict';

angular.module('inventory.controllers')
  .controller('InventorySidebarCtrl', ['$rootScope', '$scope', 'productService', 'partsService', 'authorizationService',
    function ($rootScope, $scope, productService, partsService, authorizationService) {
      $scope.token = authorizationService.token();

      $scope.reloadProducts = function () {
        productService.products($scope.token)
          .success(function (data) {
            $scope.products = data.results;
          });
      };

      $rootScope.$watch('isLoggedIn', function (newVal) {
        if (newVal) {
          $scope.token = authorizationService.token();
          $scope.reloadProducts();
        } else {
          $scope.token = null;
          $scope.products = {};
        }
      });

      $rootScope.$watch('isProductRemoved', function (newVal) {
        if (newVal) {
          $scope.reloadProducts();
          $rootScope.isProductRemoved = false;
        }
      });

      $scope.isProductAdded = false;

      $scope.openNewProductForm = function () {
        $scope.productForm = {};

        $scope.productFormHeader = 'Add Product';

        $scope.isNewProductForm = true;

        var modalInstance = productService.openProductForm($scope);

        $scope.addProduct = function () {
          productService.addProduct(
            $scope.productForm.name,
            $scope.productForm.description,
            $scope.token
          )
            .success(function (product) {
              $scope.isProductAdded = true;

              $scope.reloadProducts();

              partsService.openNewPartsForm($scope, product.id, null);

              modalInstance.close();
            });
        };
      };
    }]);
