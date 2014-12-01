'use strict';

angular.module('inventory.controllers')
  .controller('InventoryTableCtrl', ['$scope', 'inventoryService', 'formService',
    function ($scope, inventoryService, formService) {
      $scope.inventories = $scope.component.inventories;

      $scope.unclassifiedQuantity = $scope.component.unclassifiedQuantity;

      $scope.openEditInventoryForm = function (inventory) {
        var modalInstance = null;

        $scope.inventoryFormInputs = {
          id: inventory.id,
          description: inventory.description,
          quantity: inventory.quantity
        };

        $scope.inventoryFormHeader = 'Edit Inventory';

        $scope.isAddInventoryForm = false;

        $scope.editInventory = function () {
          inventoryService.edit($scope.inventoryFormInputs)
            .then(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };

        $scope.removeInventoryAlert = function () {
          var removeAlertInstance = null;

          modalInstance.close();

          $scope.removeAlertMessage = 'Are you sure to remove the inventory?';

          $scope.removeAlertFunc = function () {
            inventoryService.remove($scope.inventoryFormInputs.id)
              .then(function () {
                $scope.reloadProduct();
                removeAlertInstance.close();
              });
          };

          removeAlertInstance = formService.openRemoveAlert($scope);
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/inventory-form.html',
          scope: $scope
        });
      };
    }]);
