'use strict';

angular.module('inventory.controllers')
  .controller('InventoryController', ['$scope', '$modal', 'inventoryService', 'removeAlert',
    function ($scope, $modal, inventoryService, removeAlert) {
      $scope.openEditInventoryForm = function (inventory) {
        var modalInstance = $modal.open({
          templateUrl: '/views/inventory/modals/inventory-form.html',
          scope: $scope
        });

        $scope.inventoryForm = {
          description: inventory.description,
          quantity: inventory.quantity
        };

        $scope.inventoryFormHeader = 'Edit Inventory';

        $scope.isNewInventoryForm = false;

        $scope.removeInventoryAlert = function () {
          var removeModalInstance = removeAlert.openRemoveAlert($scope);

          $scope.removeAlertMessage = 'Are you sure to remove the inventory?';

          modalInstance.close();

          $scope.removeAlertFunc = function () {
            $scope.removeInventory();
          };

          $scope.removeInventory = function () {
            inventoryService.removeInventory(inventory.id, $scope.token)
              .success(function () {
                $scope.reloadProduct();
                removeModalInstance.close();
              });
          };
        };

        $scope.editInventory = function () {
          inventoryService.editInventory(
            inventory.id,
            $scope.inventoryForm.description,
            $scope.inventoryForm.quantity,
            $scope.token
          )
            .success(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };
      };
    }]);