'use strict';

angular.module('inventory.controllers')
  .controller('ProductsCtrl', ['$scope', '$location', '$routeParams', 'productService', 'componentService', 'formService', 'product',
    function ($scope, $location, $routeParams, productService, componentService, formService, product) {
      $scope.reloadProduct = function () {
        productService.show($routeParams.productId)
          .then(function (product) {
            $scope.product = product;
            $scope.components = $scope.product.components;
          }, function () {
            $location.path('/');
          });
      };

      $scope.product = product;
      $scope.components = product.components;

      $scope.openEditProductForm = function () {
        var modalInstance = null;

        $scope.productFormInputs = {
          id: $scope.product.id,
          name: $scope.product.name,
          description: $scope.product.description
        };

        $scope.productFormHeader = 'Edit Product';

        $scope.isAddProductForm = false;

        $scope.editProduct = function () {
          productService.edit($scope.productFormInputs)
            .then(function () {
              $scope.reloadProduct();
              $scope.$broadcast('productEdited');
              modalInstance.close();
            });
        };

        $scope.removeProductAlert = function () {
          var removeAlertInstance = null;

          modalInstance.close();

          $scope.removeAlertMessage = 'Are you sure to remove the product?';

          $scope.removeAlertFunc = function () {
            productService.remove($scope.productFormInputs.id)
              .then(function () {
                removeAlertInstance.close();
                $location.path('/');
              });
          };

          removeAlertInstance = formService.openRemoveAlert($scope);
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/product-form.html',
          scope: $scope
        });
      };

      $scope.openAddComponentForm = function (product) {
        var modalInstance = null;

        $scope.componentFormInputs = {
          productId: product.id
        };

        $scope.componentFormHeader = 'Add Component';

        $scope.isAddComponentForm = true;

        $scope.addComponent = function () {
          componentService.add($scope.componentFormInputs)
            .then(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/component-form.html',
          scope: $scope
        });
      };
    }]);
