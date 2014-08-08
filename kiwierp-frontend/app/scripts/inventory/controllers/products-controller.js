'use strict';

angular.module('inventory.controllers')
  .controller('ProductsCtrl', ['$rootScope', '$scope', '$location', 'productService', 'partsService', 'authorizationService', 'removeAlert',
    function ($rootScope, $scope, $location, productService, partsService, authorizationService, removeAlert) {
      $scope.token = authorizationService.token();

      $scope.reloadProduct = function () {
        productService.product($scope.token)
          .success(function (product) {
            $scope.product = product;
          });
      };

      $scope.$watch('product', function (newVal) {
        $scope.isPartsExist = function () {
          return !!newVal && !!newVal.partsList && newVal.partsList.length > 0;
        };
      });

      $rootScope.$watch('isLoggedIn', function (newVal) {
        if (newVal) {
          $scope.token = authorizationService.token();
          $scope.reloadProduct();
        } else {
          $scope.token = null;
          $scope.product = {};
        }
      });

      $scope.openEditProductForm = function () {
        var modalInstance = productService.openEditProductForm($scope);

        $scope.removeProductAlert = function () {
          var removeModalInstance = removeAlert.openRemoveAlert($scope);

          $scope.removeAlertMessage = 'Are you sure to remove the product?';

          modalInstance.close();

          $scope.removeAlertFunc = function () {
            $scope.removeProduct();
          };

          $scope.removeProduct = function () {
            productService.removeProduct($scope.product.id, $scope.token)
              .success(function () {
                removeModalInstance.close();
                $location.path('/');
              });
          };

          $scope.editProduct = function () {
            productService.editProduct(
              $scope.product.id,
              $scope.productForm.name,
              $scope.productForm.description,
              $scope.token
            )
              .success(function () {
                $scope.reloadProduct();
                modalInstance.close();
              });
          };
        }
      };

      $scope.openNewPartsForm = function () {
        partsService.openNewPartsForm($scope, $scope.product.id);
      };

    }]);
