'use strict';

angular.module('inventory.controllers')
  .controller('InventoryIndexCtrl', ['$scope', 'productService', 'productInventoryService', 'formService', 'products',
    function ($scope, productService, productInventoryService, formService, products) {
      var modifyProducts = function (pros) {
        return pros.map(function (p) {
          var modifiedInventories = {};

          p.inventories.forEach(function (i) {
            var status = i['status'];

            if (!!modifiedInventories[status]) {
              modifiedInventories[status].inventories.push(i);
            } else {
              modifiedInventories[status] = {
                name: status,
                inventories: [i]
              };
            }
          });

          p.modifiedInventories = modifiedInventories;
          return p;
        });
      };

      var reloadProducts = function () {
        productService.search()
          .then(function (data) {
            $scope.products = modifyProducts(data.results);
          });
      };

      $scope.products = modifyProducts(products);

      $scope.totalQuantity = function (results) {
        var quantitySum = 0;

        results.forEach(function (result) {
          quantitySum += result.quantity;
        });

        return quantitySum;
      };

      $scope.openAddInventoryForm = function (product) {
        var modalInstance = null;

        $scope.inventoryFormInputs = {
          productId: product.id
        };

        $scope.inventoryFormHeader = 'Add Inventory';

        $scope.isAddInventoryForm = true;

        $scope.addInventory = function () {
          productInventoryService.add($scope.inventoryFormInputs)
            .then(function () {
              reloadProducts();
              modalInstance.close();
            });
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/product-inventory-form.html',
          scope: $scope
        });
      };

      $scope.openEditInventoryForm = function (productId, inventory) {
        var modalInstance = null;

        $scope.inventoryFormInputs = {
          id: inventory.id,
          description: inventory.description,
          status: inventory.status,
          quantity: inventory.quantity
        };

        $scope.inventoryFormHeader = 'Edit Inventory';

        $scope.isAddInventoryForm = false;

        $scope.editInventory = function () {
          productInventoryService.edit(productId, $scope.inventoryFormInputs)
            .then(function () {
              reloadProducts();
              modalInstance.close();
            });
        };

        $scope.removeInventoryAlert = function () {
          var removeAlertInstance = null;

          modalInstance.close();

          $scope.removeAlertMessage = 'Are you sure to remove the inventory?';

          $scope.removeAlertFunc = function () {
            productInventoryService.remove(productId, $scope.inventoryFormInputs.id)
              .then(function () {
                reloadProducts();
                removeAlertInstance.close();
              });
          };

          removeAlertInstance = formService.openRemoveAlert($scope);
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/product-inventory-form.html',
          scope: $scope
        });
      };
    }]);
