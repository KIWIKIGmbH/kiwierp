'use strict';

angular.module('inventory.controllers')
  .controller('InventorySidebarCtrl', ['$scope', '$location', 'productService', 'formService',
    function ($scope, $location, productService, formService) {
      var reloadProducts = function () {
        productService.search()
          .then(function (data) {
            $scope.products = data.results;
          });
      };

      reloadProducts();

      $scope.$on('productEdited', function () {
        reloadProducts();
      });

      $scope.openAddProductForm = function () {
        var modalInstance = null;

        $scope.productFormInputs = {};

        $scope.productFormHeader = 'Add Product';

        $scope.isAddProductForm = true;

        $scope.addProduct = function () {
          productService.add($scope.productFormInputs)
            .then(function (product) {
              reloadProducts();
              modalInstance.close();
              $location.path('/inventories/' + product.id);
            });
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/product-form.html',
          scope: $scope
        });
      };
    }]);
